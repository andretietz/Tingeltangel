package com.andretietz.audiopen.explode

import com.andretietz.audiopen.data.BookData
import com.andretietz.audiopen.data.BookDataItem
import com.andretietz.audiopen.data.DataFileExploder
import java.io.File
import java.io.FileNotFoundException
import java.io.RandomAccessFile

class OufExploder(
  private val cacheDir: File
) : DataFileExploder {

  override fun explode(file: File): BookData {
    if (!file.exists()) throw FileNotFoundException("Couldn't find file: $file")

    val accessFile = RandomAccessFile(file, "r")

    // HEADER
    val indexTable: Long = accessFile.readInt().toLong()
    accessFile.skipBytes(4)
    val smallestMediaId: Int = accessFile.readInt()
    accessFile.skipBytes(4)
    val numberOfItems: Int = accessFile.readInt()
    val bookId: Int = accessFile.readInt()

    accessFile.seek(indexTable)
    return BookData(bookId, extractBookDataItems(bookId, numberOfItems, smallestMediaId, accessFile))
  }


  private fun extractBookDataItems(
    bookId: Int,
    itemCount: Int,
    mediaId: Int,
    accessFile: RandomAccessFile
  ): List<BookDataItem> {
    val targetDir = File(cacheDir, "$bookId").also { it.mkdir() }
    return (0..itemCount)
      .map { id ->
        id + mediaId to IndexTableItem(
          Foo.getPositionFromCode(accessFile.readInt(), id),
          accessFile.readInt(),
          accessFile.readInt()
        )
      }.toMap()
      .map { (code, tableItem) ->
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
            BookDataItem.Script(code)
          }
          else -> {
            // TODO: log error
            throw Error()
          }
        }
      }
  }

  companion object {
    /*
    public static int getPositionInFileFromCode(int code, int n) {
      if (((code & 255) != 0) | (n < 0)) {
        return -1;
      }
      n--;
      code = code >> 8;
      int c = ((code >> 3) & 1) | (((code >> 4) & 1) << 1) | (((code >> 5) & 1) << 2) | (((code >> 7) & 1) << 3) | (((code >> 9) & 1) << 4);
      code -= n * 26 - E[c];
      return code << 8;
    }
     */
    fun getPositionFromCode(code: Int, n: Int): Long {
      if (((code and 0xFF) != 0) or (n < 0)) {
        return -1
      }
      val co = code shr 8
      // ((code >> 3) & 1) | (((code >> 4) & 1) << 1) | (((code >> 5) & 1) << 2) | (((code >> 7) & 1) << 3) | (((code >> 9) & 1) << 4);
      val c = ((co shr 3) and 1) or
          (((co shr 4) and 1) shl 1) or
          (((co shr 5) and 1) shl 2) or
          (((co shr 7) and 1) shl 3) or
          (((co shr 9) and 1) shr 4)
      val a = ((code - (((n - 1) * 26) - LOOKUP_TABLE[c])) shl 8).toLong()
      if(a<0) {
        println(a)
      }
      return a
    }

    private val LOOKUP_TABLE = arrayOf(
      578, 562, 546, 530, 514, 498, 482, 466, 322, 306, 290, 274, 258, 242, 226, 210, -446, -462, -478, -494, -510, -526, -542, -558, -702, -718, -734, -750, -766, -782, -798, -814
//      0x232,
//      0x222,
//      0x212,
//      0x202,
//      0x1f2,
//      0x1e2,
//      0x1d2,
//      0x142,
//      0x132,
//      0x122,
//      0x112,
//      0x102,
//      0xf2,
//      0xe2,
//      0xd2,
//      0xfffffe42,
//      0xfffffe32,
//      0xfffffe22,
//      0xfffffe12,
//      0xfffffe02,
//      0xfffffdf2,
//      0xfffffde2,
//      0xfffffdd2,
//      0xfffffd42,
//      0xfffffd32,
//      0xfffffd22,
//      0xfffffd12,
//      0xfffffd02,
//      0xfffffcf2,
//      0xfffffce2,
//      0xfffffcd2
    )
  }

  data class IndexTableItem(
    val position: Long,
    val size: Int,
    val type: Int
  )
}
