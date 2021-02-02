package com.andretietz.audiopen.explode

import com.andretietz.audiopen.data.BookData
import com.andretietz.audiopen.data.BookDataItem
import com.andretietz.audiopen.data.DataFileDisassembler
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
        1 -> BookDataItem.MP3(code, File(targetDir, "$code.mp3")
          .also { file ->
            file.createNewFile()
            file.writeBytes(buffer)
          })
        2 -> { // script
          // TBD
          BookDataItem.Script(
            code,
            scriptDisassembler.disassemble(buffer.inputStream())
          )
        }
        else -> {
          // TODO: log error?
          null
        }
      }
    }
  }

  companion object {
    fun getPositionFromCode(code: Int, n: Int): Int {
      if (((code and 0xFF) != 0) || (n < 0)) return -1

      val co = code shr 8 // 0x00F0 -> 0x000F
      val c = co shr 3 and 1 or
        (co shr 4 and 1 shl 1) or
        (co shr 5 and 1 shl 2) or
        (co shr 7 and 1 shl 3) or
        (co shr 9 and 1 shl 4)
      val a = co - (((n - 1) * 26) - LOOKUP_TABLE[c])

      return a shl 8 // 0x000F -> 0x00F0
    }

    /**
     * I have no clue how this numbers come together. It just works. Thanks to Martin
     */
    private val LOOKUP_TABLE = arrayOf(
      // @formatter:off
      578, 562, 546, 530, 514, 498, 482, 466, 322, 306, 290,
      274, 258, 242, 226, 210, -446, -462, -478, -494, -510,
      -526, -542, -558, -702, -718, -734, -750, -766, -782, -798, -814
      // @formatter:on
    )

    private val scriptDisassembler = OufScriptDisassembler()
  }

  data class IndexTableItem(
    val position: Long,
    val size: Int,
    val type: Int
  )
}
