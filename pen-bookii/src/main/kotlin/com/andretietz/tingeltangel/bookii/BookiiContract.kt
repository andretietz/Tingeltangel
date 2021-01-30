package com.andretietz.tingeltangel.bookii

import com.andretietz.tingeltangel.pencontract.AudioPenContract
import com.andretietz.tingeltangel.pencontract.AudioPenDevice
import com.andretietz.tingeltangel.pencontract.Book
import com.andretietz.tingeltangel.pencontract.BookInfo
import com.andretietz.tingeltangel.pencontract.BookSource
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File

class BookiiContract(
  httpClient: OkHttpClient,
  cacheDir: File
) : AudioPenContract {

  private val api by lazy {
    Retrofit.Builder()
      .baseUrl(API_BASE_URL)
      .addConverterFactory(MoshiConverterFactory.create())
      .client(httpClient)
      .build().create(BookiiApi::class.java)
  }

  private val cacheDir = File(cacheDir, TYPE).apply {
    if (!exists() && !mkdirs()) {
      throw IllegalStateException("Cannot create cache folder: \"$absolutePath\"")
    }
  }

  override fun source(): BookSource = BookiiBookSource(api, cacheDir)

  override fun verifyDevice(rootFolder: File): Boolean {
    val bookiiDirs = rootFolder.listFiles()
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
//
//
//
//    val bookFiles = bookDir.listFiles()?.filter { it.name.endsWith("kii") }
//    val imageFiles = bookDir.listFiles()?.filter { it.name.endsWith("png") }
//
//    return emptyList()
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
      TYPE,
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

  private fun createInfoFile(info: BookInfo): String {
    val sb = StringBuilder()
    sb.appendLine("$SETTINGS_NAME: ${info.title}")
    sb.appendLine("$SETTINGS_BOOK_PUBLISHER: ${info.publisherName}")
    sb.appendLine("$SETTINGS_BOOK_AUTHOR: ${info.authorName}")
    sb.appendLine("$SETTINGS_BOOK_VERSION: ${info.version}")
    sb.appendLine("$SETTINGS_URL: ${info.image?.toString() ?: ""}")
    sb.appendLine("$SETTINGS_THUMB_MD5:") // TODO: could be generated
    sb.appendLine("$SETTINGS_FILE_MD5:") // TODO: could be generated
    sb.appendLine("$SETTINGS_BOOK_AREA_CODE: ${info.areaCode}")
    sb.appendLine("$SETTINGS_TYPE: ${info.mediaType}")
    sb.appendLine("$SETTINGS_ISBN: ${info.isbn ?: ""}")
    sb.appendLine("$SETTINGS_VOLUME: ${info.volume}")
    return sb.toString()
  }


  override val type = AudioPenContract.Type(NAME, TYPE)

  companion object {
    internal const val TYPE = "bookii"
    internal const val NAME = "Bookii"
    private const val API_BASE_URL = "https://www.bookii-medienservice.de/Medienserver-1.0/api/"

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
