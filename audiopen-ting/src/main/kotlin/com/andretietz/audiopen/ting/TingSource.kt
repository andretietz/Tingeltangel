package com.andretietz.audiopen.ting

import com.andretietz.audiopen.BookDisplay
import com.andretietz.audiopen.Thumbnail
import com.andretietz.audiopen.remote.BookSource
import com.andretietz.audiopen.ting.pen.DeviceBook
import okhttp3.HttpUrl.Companion.toHttpUrl
import java.io.File

class TingSource(
  private val cacheDir: File
) : BookSource {

  override val type = Ting.AUDIOPEN_TYPE

  init {
    if (!cacheDir.exists()) {
      cacheDir.mkdirs()
    }
  }

  override suspend fun availableBooks(): List<BookDisplay> {
    return Ting.KNOWN_BOOKS
      .mapNotNull { id ->
        val file = infoFile(id)
        if (file == null) {
          println("Problem with $id (${file})")
          return@mapNotNull null
        }
        createDeviceBook(id, extractInfo(file))
      }.map {
        BookDisplay(
          it.id,
          type,
          it.title,
          Thumbnail.Remote(
            THUMBNAIL_FILE.format(
              it.id.toInt(),
              it.areaCode,
              it.id.padStart(5, '0'),
              it.areaCode
            ).toHttpUrl().toUrl()
          )
        )
      }
  }

  private fun createDeviceBook(id: Int, map: Map<String, String>): DeviceBook? {
    map[SETTINGS_BOOK_AREA_CODE] ?: return null
    return DeviceBook(
      id.toString(),
      map[SETTINGS_NAME] ?: return null,
      map[SETTINGS_BOOK_PUBLISHER] ?: "",
      map[SETTINGS_BOOK_AUTHOR] ?: "",
      map[SETTINGS_BOOK_VERSION]?.toIntOrNull() ?: return null,
      map[SETTINGS_URL] ?: "",
      map[SETTINGS_THUMB_MD5] ?: return null, // TBD: is this required?
      map[SETTINGS_FILE_MD5] ?: return null, // TBD: is this required?
      map[SETTINGS_BOOK_AREA_CODE]!!
    )
  }

  private fun extractInfo(file: File): Map<String, String> {
    return file.readLines()
      .map {
        val item = it.split(":")
        item[0].trim() to item[1].trim()
      }.toMap()
  }

  private fun infoFile(id: Int): File? {
    val url = INFO_FILE.format(id, DEFAULT_AREA_CODE, id.toString().padStart(5, '0'), DEFAULT_AREA_CODE)
    val infoFile = File(cacheDir, "%s_%s.txt".format(id.toString().padStart(5, '0'), DEFAULT_AREA_CODE))
    if (!infoFile.exists()) {
      url.toHttpUrl().toUrl().openStream().use { input ->
        infoFile.outputStream().use { output -> input.copyTo(output) }
      }
    }
    return infoFile
  }

  companion object {
    // mid, areacode, mid(5digit string), areacode
    const val INFO_FILE = "${Ting.API_BASE_URL}/get-description/id/%d/area/%s/sn/5497559973888/%s_%s.txt"
    const val THUMBNAIL_FILE =
      "${Ting.API_BASE_URL}/get/id/%d/type/thumb/area/%s/sn/5497559973888/%s_%s.png"
    const val BOOK_FILE = "${Ting.API_BASE_URL}/get-description/id/%d/area/%s/type/archive/sn/5497559973888/%s_%s.ouf"
    const val DEFAULT_AREA_CODE = "en"


    private const val SETTINGS_NAME = "Name"
    private const val SETTINGS_BOOK_AREA_CODE = "Book Area Code"
    private const val SETTINGS_BOOK_VERSION = "Book Version"
    private const val SETTINGS_BOOK_PUBLISHER = "Publisher"
    private const val SETTINGS_BOOK_AUTHOR = "Author"
    private const val SETTINGS_URL = "URL"
    private const val SETTINGS_THUMB_MD5 = "ThumbMD5"
    private const val SETTINGS_FILE_MD5 = "FileMD5"
  }
}
