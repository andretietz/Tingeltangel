package com.andretietz.tingeltangel.bookii

import com.andretietz.tingeltangel.pencontract.AudioPenContract
import com.andretietz.tingeltangel.pencontract.AudioPenDevice
import com.andretietz.tingeltangel.pencontract.Book
import com.andretietz.tingeltangel.pencontract.BookInfo
import com.andretietz.tingeltangel.pencontract.BookSource
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

  override fun booksFromDevice(device: AudioPenDevice): List<Book> {
    val bookDir = device.rootDirectory.listFiles()?.firstOrNull { it.name == DIR_BOOK } ?: return emptyList()

//    BookInfo(
//
//      )

    val bookFiles = bookDir.listFiles()?.filter { it.name.endsWith("kii") }
    val imageFiles = bookDir.listFiles()?.filter { it.name.endsWith("png") }
    val infoFiles = bookDir.listFiles()?.filter { it.name.endsWith("txt") }

    return emptyList()
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


  }
}
