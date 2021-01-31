package com.andretietz.audiopen.bookii

import com.andretietz.audiopen.AudioPenDevice
import com.andretietz.audiopen.Book
import com.andretietz.audiopen.BookInfo
import com.andretietz.audiopen.device.DeviceManager
import java.io.File

class BookiiDeviceManager : DeviceManager {

  override val type = Bookii.AUDIOPEN_TYPE

  override fun verifyDevice(rootDir: File): Boolean {
    val bookiiDirs = rootDir.listFiles()
      ?.filter { it.name == DIR_BOOK && it.isDirectory || it.name == DIR_CONFIG && it.isDirectory } ?: return false
    if (bookiiDirs.size != 2) return false
    val configFile = bookiiDirs.first { it.name == DIR_CONFIG }.listFiles()
      ?.firstOrNull { it.name == FILE_SETTINGS } ?: return false
    val tbdFile = bookiiDirs.first { it.name == DIR_CONFIG }.listFiles()
      ?.firstOrNull { it.name == FILE_TBD } ?: return false
    return configFile.exists() && tbdFile.exists()
  }

  override suspend fun booksFromDevice(device: AudioPenDevice): List<Book> {
    val bookDir = device.rootDirectory.listFiles()?.firstOrNull { it.name == DIR_BOOK } ?: return emptyList()

    val infoFiles = bookDir.listFiles()?.filter { infoFileRegex.matches(it.name) }
      ?.mapNotNull { parseInfoFile(it) } ?: emptyList()

    return infoFiles.map { Book(it) }
  }

  private fun parseInfoFile(file: File): BookInfo? {
    val (id, _) = infoFileRegex.find(file.name)?.destructured ?: return null

    val map = mutableMapOf<String, String>()

    file.readLines()
      .forEach {
        val item = it.split(":")
        map.putIfAbsent(item[0].trim(), item[1].trim())
      }
    return BookInfo(
      id.toInt().toString(),
      Bookii.AUDIOPEN_TYPE,
      map[SETTINGS_NAME] ?: return null,
      map[SETTINGS_BOOK_AREA_CODE] ?: return null,
      map[SETTINGS_BOOK_VERSION]?.toIntOrNull() ?: return null,
      File(file.parentFile, "${file.nameWithoutExtension}.png").toURI().toURL(),
      file.toURI().toURL(),
      map[SETTINGS_BOOK_PUBLISHER] ?: "",
      map[SETTINGS_BOOK_AUTHOR] ?: "",
      map[SETTINGS_TYPE] ?: "Buch",
      map[SETTINGS_VOLUME]?.toIntOrNull() ?: 0,
      map[SETTINGS_ISBN] ?: ""
    )
  }

  @SuppressWarnings("Detekt.UnusedPrivateMember")
  private fun createInfoFile(info: BookInfo): String {
    return StringBuilder().apply {
      appendLine("$SETTINGS_NAME: ${info.title}")
      appendLine("$SETTINGS_BOOK_PUBLISHER: ${info.publisherName}")
      appendLine("$SETTINGS_BOOK_AUTHOR: ${info.authorName}")
      appendLine("$SETTINGS_BOOK_VERSION: ${info.version}")
      appendLine("$SETTINGS_URL: ${info.image?.toString() ?: ""}")
      appendLine("$SETTINGS_THUMB_MD5:") // TBD: could be generated
      appendLine("$SETTINGS_FILE_MD5:") // TBD: could be generated
      appendLine("$SETTINGS_BOOK_AREA_CODE: ${info.areaCode}")
      appendLine("$SETTINGS_TYPE: ${info.mediaType}")
      appendLine("$SETTINGS_ISBN: ${info.isbn ?: ""}")
      appendLine("$SETTINGS_VOLUME: ${info.volume}")
    }.toString()
  }

  companion object {
    private const val DIR_BOOK = "book"
    private const val DIR_CONFIG = "configure"
    private const val FILE_SETTINGS = "settings.ini"
    private const val FILE_TBD = "tbd.txt"

    private val infoFileRegex = Regex("^([0-9]{5})\\_([a-z]{2})\\.txt\$")

    private const val SETTINGS_NAME = "Name"
    private const val SETTINGS_BOOK_AREA_CODE = "Book Area Code"
    private const val SETTINGS_BOOK_VERSION = "Book Version"
    private const val SETTINGS_BOOK_PUBLISHER = "Publisher"
    private const val SETTINGS_BOOK_AUTHOR = "Author"
    private const val SETTINGS_URL = "URL"
    private const val SETTINGS_THUMB_MD5 = "ThumbMD5"
    private const val SETTINGS_FILE_MD5 = "FileMD5"
    private const val SETTINGS_TYPE = "Type"
    private const val SETTINGS_ISBN = "ISBN"
    private const val SETTINGS_VOLUME = "Volume"
  }
}
