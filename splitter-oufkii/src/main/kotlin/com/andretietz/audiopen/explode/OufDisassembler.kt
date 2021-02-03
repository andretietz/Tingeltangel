package com.andretietz.audiopen.explode

import com.andretietz.audiopen.LoggerDelegate
import com.andretietz.audiopen.data.BookData
import com.andretietz.audiopen.data.BookDataItem
import com.andretietz.audiopen.data.DataFileDisassembler
import com.andretietz.audiopen.explode.CodePositionHelper.getPositionFromCode
import com.andretietz.audiopen.explode.script.OufScriptDisassembler
import java.io.File
import java.io.FileNotFoundException
import java.io.RandomAccessFile

class OufDisassembler(
  private val cacheDir: File
) : DataFileDisassembler {

  override fun disassemble(file: File): BookData {
    if (!file.exists()) throw FileNotFoundException("Couldn't find file: $file")

    val accessFile = RandomAccessFile(file, "r")

    // HEADER
    val indexTable: Int = accessFile.readInt()
    accessFile.skipBytes(4)
    val smallestMediaId: Int = accessFile.readInt()
    accessFile.skipBytes(4)
    val numberOfItems: Int = accessFile.readInt()
    val bookId: Int = accessFile.readInt()

    accessFile.seek(indexTable.toLong())


    return BookData(bookId, extractBookDataItems(bookId, numberOfItems, smallestMediaId, accessFile))
  }


  private fun extractBookDataItems(
    bookId: Int,
    itemCount: Int,
    mediaId: Int,
    accessFile: RandomAccessFile
  ): List<BookDataItem> {
    val targetDir = File(cacheDir, "$bookId").also { it.mkdir() }
    val map = (0 until itemCount)
      .map { id ->
        id + mediaId to IndexTableItem(
          position = getPositionFromCode(accessFile.readInt(), id).toLong(),
          size = accessFile.readInt(),
          type = accessFile.readInt()
        )
      }.toMap()

    return map.mapNotNull { (code, tableItem) ->
      accessFile.seek(tableItem.position)
      val buffer = ByteArray(tableItem.size)
      accessFile.read(buffer)
      when (tableItem.type) {
        BookDataItem.TYPE_AUDIO -> BookDataItem.MP3(code, tableItem.size, File(targetDir, "$code.mp3")
          .also { file ->
            file.createNewFile()
            file.writeBytes(buffer)
          })
        BookDataItem.TYPE_SCRIPT -> {
          BookDataItem.Script(
            code,
            tableItem.size,
            scriptDisassembler.disassemble(buffer.inputStream())
          )
        }
        else -> {
          logger.debug("Invalid data type in index table: ${tableItem.type}")
          null
        }
      }
    }
  }

  companion object {
    private val logger by LoggerDelegate()
    private val scriptDisassembler = OufScriptDisassembler()
  }

  data class IndexTableItem(
    val position: Long,
    val size: Int,
    val type: Int
  )
}
