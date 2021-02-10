package com.andretietz.audiopen.assembler

import com.andretietz.audiopen.LoggerDelegate
import com.andretietz.audiopen.data.Book
import com.andretietz.audiopen.data.BookItem
import com.andretietz.audiopen.data.DataFileDisassembler
import java.io.ByteArrayInputStream
import java.io.DataInputStream
import java.io.File
import java.io.FileInputStream
import java.lang.Integer.min

class OufDisassembler(
  private val cacheDir: File
) : DataFileDisassembler {

  override fun disassemble(file: File): Book {
    DataInputStream(FileInputStream(file)).use { inputStream ->
      val header = readHeader(inputStream)
      val indexTable = readIndexTable(inputStream, header)
      val data = readItems(inputStream, file, header, indexTable)
      return Book(header.id, data)
    }
  }

  /**
   * @return Header information about the ouf-file.
   */
  private fun readHeader(inputStream: DataInputStream): Header {
    val indexTable: Int = inputStream.readInt()
    if (indexTable != 0x66) { // TODO: check if there's actually a book returning 0x66 (legacy code, not documented but converted)
      inputStream.readInt().also {
        logger.debug("Skipping (1): $it or 0x${Integer.toHexString(it)}")
      }
    }
    val mediaIdStart: Int = inputStream.readInt()
    val mediaIdEnd: Int = inputStream.readInt()
    val mediaCount: Int = inputStream.readInt()
    val bookId: Int = inputStream.readInt()
    inputStream.readInt() // magic value
    inputStream.readInt() // date
    inputStream.readInt() // 0x00
    inputStream.readInt() // 0xFF

    inputStream.skipBytes(indexTable - 40)

    val readHeader = Header(bookId, mediaCount, mediaIdStart, mediaIdEnd, indexTable, false)

    val newHeader = if (mediaIdStart != 15001) {
      if (mediaIdStart == 15000) {
        logger.warn("first mediaId is 15000. Trying auto correction...")
        inputStream.readInt()
        inputStream.readInt()
        val type15000: Int = inputStream.readInt()
        if (type15000 != 0) {
          logger.warn("Auto correction failed. The import is expected to fail... Still trying...")
          readHeader
        } else {
          logger.info("Auto correction successful!")
          readHeader.copy(count = readHeader.count - 1, mediaIdStart = 15001, correctedStartId = true)
        }
      } else {
        logger.warn("first ting id is neither 15001 nor 15000. The import is expected to fail... Still trying...")
        readHeader
      }
    } else readHeader
    if (newHeader.count != newHeader.mediaIdEnd - newHeader.mediaIdStart + 1) {
      throw IllegalStateException("index count mismatch $newHeader") // might be worth, converting to a warning?
    }
    return newHeader
  }

  /**
   * @return The index table within the ouf-file.
   */
  private fun readIndexTable(input: DataInputStream, header: Header): Set<IndexTableItem> {
    var foundFirstEntryCode = false
    // read index table
    return (header.mediaIdStart..header.mediaIdEnd).mapIndexed { index, id ->
      val position = input.readInt()
      val size = input.readInt()
      val type = input.readInt()
      if (!foundFirstEntryCode && size > 0 && type != 0) {
        foundFirstEntryCode = true
        IndexTableItem(id, position, size, type, index, true)
      } else {
        IndexTableItem(id, position, size, type, index, false)
      }
    }.filter { it.type != 0 }
      .toSet()
  }

  /**
   * Reads the items of an ouf-file. Since there are no official docs, it searches for the right position
   * of the items within the file.
   *
   * @return a Set of [BookItem]s of the file.
   */
  private fun readItems(
    inputStream: DataInputStream,
    file: File,
    header: Header,
    indexTable: Set<IndexTableItem>
  ): Set<BookItem> {
    // indexTable position + (3*int_size) * (header.mediaIdEnd - header.mediaIdStart + 1)
    val endOfIndex = header.indexTable + (12 * header.count)
    val spacing = (0x100 - (endOfIndex % 0x100)) % 0x100
    val potentialDataStart = endOfIndex + spacing
    // when start-id was corrected we read 3 additional integers, which have to be subtracted from spacing.
    inputStream.skipBytes(if (header.correctedStartId) spacing - 12 else spacing)

    val startItem =
      indexTable.firstOrNull { it.isStartItem } ?: throw IllegalStateException("No first element found")

    logger.info("firstEntryCode: ${startItem.id}")
    logger.info("firstEntryLength: ${startItem.size}")

    val itemOffset = searchForDataStart(inputStream, potentialDataStart, startItem)

    logger.debug("Main-Stream: available bytes: ${inputStream.available()}")
    val bookDirectory = File(cacheDir, "${header.id}").also { it.mkdirs() }
    return indexTable.map { tableItem ->
      val itemPosition = CodePositionHelper.getPositionFromCode(tableItem.position, tableItem.id - 15001) + itemOffset
      DataInputStream(FileInputStream(file)).use { itemFileInputStream ->
        itemFileInputStream.skipNBytes(itemPosition.toLong())
        bookDataItem(itemFileInputStream, tableItem, bookDirectory)
      }
    }.toSet()
  }

  private fun bookDataItem(inputStream: DataInputStream, tableItem: IndexTableItem, targetDir: File): BookItem {
    val fileName = tableItem.id.toString().padStart(5, '0')
    return when (tableItem.type) {
      BookItem.TYPE_AUDIO -> {
        val audioDirectory = File(targetDir, SUBDIR_AUDIO).also { if (!it.exists()) it.mkdirs() }
        val file = File(audioDirectory, "$fileName.mp3")
        var bytesToRead = tableItem.size
        file.outputStream().use { output ->
          val buffer = ByteArray(4096)
          var n: Int
          while (inputStream.read(buffer, 0, min(buffer.size, bytesToRead)).also { n = it } > 0) {
            output.write(buffer, 0, n)
            bytesToRead -= n
          }
        }
        BookItem.MP3(tableItem.id, file, file.inputStream().use { !isMp3Data(it.readNBytes(4)) })
      }
      BookItem.TYPE_SCRIPT -> {
        val scriptDirectory = File(targetDir, SUBDIR_SCRIPT).also { if (!it.exists()) it.mkdirs() }
        val binFile = File(scriptDirectory, "$fileName.bin")
        val srcFile = File(scriptDirectory, "$fileName.txt")
        val scriptContent = inputStream.readNBytes(tableItem.size)
        binFile.writeBytes(scriptContent)
        val disassembled = disassembleScript(ByteArrayInputStream(scriptContent))
        srcFile.writeText(disassembled)
        val isSubRoutine = disassembled.reader().buffered().useLines { it.first() } == SCRIPT_RETURN
        BookItem.Script(tableItem.id, disassembleScript(ByteArrayInputStream(scriptContent)), isSubRoutine)
      }
      else -> throw IllegalStateException("Cannot recognize type: ${tableItem.type}")
    }
  }

  private fun searchForDataStart(
    inputStream: DataInputStream,
    searchOffSet: Int,
    startItem: IndexTableItem
  ): Int {
    var dataStart = searchOffSet
    // reading max 50 bytes
    val buffer = inputStream.readNBytes(startItem.size.coerceAtMost(50))

    // searching in binary for the actual start...
    if (startItem.type == BookItem.TYPE_SCRIPT) {
      logger.info("searching for script...")
      while (!isScriptData(buffer)) { // search until
        dataStart += 0x100
        inputStream.skipBytes(0x100 - buffer.size)
        var k = 0
        while (k != buffer.size) {
          k += inputStream.read(buffer, k, buffer.size - k)
        }
      }
    } else {
      logger.info("searching for mp3...")
      while (!isMp3Data(buffer)) {
        dataStart += 0x100
        inputStream.skipBytes(0x100 - buffer.size)
        var k = 0
        while (k != buffer.size) {
          k += inputStream.read(buffer, k, buffer.size - k)
        }
      }
    }
    return dataStart - CodePositionHelper.getPositionFromCode(startItem.position, startItem.itemNumber)
  }

  private fun isScriptData(data: ByteArray): Boolean {
    val intData = data.map { it.toInt() }
    if (data.size > 3
      && intData[0] == 0
      && intData[1] == 0
      && intData[2] == 0
      && intData[3] == 0
    ) {
      return false
    }
    var p = 0
    while (p + 1 < data.size) {
      val opcode: Int = intData[p + 1] and 0xff or ((intData[p] and 0xff) shl 0x8)
      // int opcode = (data[p + 1] & 0xff) | ((data[p] & 0xff) << 8);
      val command: Command = Command.find(opcode) ?: return false
      p += command.argumentCount * 2 + 2
    }
    return true
  }

  private fun isMp3Data(data: ByteArray): Boolean {
    if (data.size <= 3) return false
    val intData = data.take(3).map { it.toInt() }
    return (
      // check for id3
      (intData[0].toChar() == 'I' && intData[1].toChar() == 'D' && intData[2].toChar() == '3') or
        // mpeg v2 layer 3 (crc)
        (((intData[0] and 0xFF) == 0xFF) && ((intData[1] and 0xFF) == 0xF2)) or
        // mpeg v2 layer 3
        (((intData[0] and 0xFF) == 0xFF) && ((intData[1] and 0xFF) == 0xF3)) or
        // mpeg v1 layer 3 (crc)
        (((intData[0] and 0xFF) == 0xFF) && ((intData[1] and 0xFF) == 0xFA)) or
        // mpeg v1 layer 3
        (((intData[0] and 0xFF) == 0xFF) && ((intData[1] and 0xFF) == 0xFB)) or
        // ? (seems to be valid)
        (((intData[0] and 0xFF) == 0xFF) && ((intData[1] and 0xFF) == 0x00)))
  }

  /**
   * decodes a file into a script.
   */
  internal fun disassembleScript(input: ByteArrayInputStream): String {
    input.use { stream -> return createScript(stream, collectLabels(stream)) }
  }

  internal fun collectLabels(stream: ByteArrayInputStream): Map<Int, Int> {
    val labels = mutableMapOf<Int, Int>()
    var currentLabel = 0
    do {
      val code = stream.readPair()
      val command = Command.find(code)
        ?: throw IllegalStateException("Unknown OpCode 0x${Integer.toHexString(code)}")
      if (command.isJump) {
        labels.putIfAbsent(stream.readPair(), ++currentLabel)
      } else {
        stream.skip(command.argumentCount * 2L) // skipping argument bytes (2 bytes per argument)
      }
    } while (stream.available() > 1)
    if (stream.available() != 1 || stream.read() != 0) {
      throw IllegalStateException("Missing end operator 0x0!")
    }
    stream.reset()
    return labels
  }

  private fun createScript(stream: ByteArrayInputStream, labels: Map<Int, Int>): String {

    val length = stream.available()
    val sb = StringBuilder()
    while (stream.available() > 1) {
      labels[length - stream.available()]?.let {
        sb.appendLine("\n:l${it}")
      }
      val command = Command.find(stream.readPair())!! // has been checked in collectJumpTargets already
      sb.append(command.asm)
      sb.append(command.disassemble(stream, labels))
    }
    return sb.toString()
  }

  companion object {
    private val logger by LoggerDelegate()
    private const val SUBDIR_AUDIO = "audio"
    private const val SUBDIR_SCRIPT = "scripts"

    private const val SCRIPT_RETURN = "return"
  }

  internal data class IndexTableItem(
    val id: Int,
    val position: Int,
    val size: Int,
    val type: Int,
    val itemNumber: Int,
    val isStartItem: Boolean
  )

  internal data class Header(
    val id: Int,
    val count: Int,
    val mediaIdStart: Int,
    val mediaIdEnd: Int,
    val indexTable: Int,
    val correctedStartId: Boolean
  )
}
