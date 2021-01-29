package com.andretietz.tingeltangel.bookii

import com.andretietz.tingeltangel.pencontract.AudioPenContract
import com.andretietz.tingeltangel.pencontract.AudioPenDevice
import com.andretietz.tingeltangel.pencontract.BookSource
import com.andretietz.tingeltangel.pencontract.PenType
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

  override suspend fun verifyDevice(rootFolder: File): AudioPenDevice? {
    return rootFolder
      .listFiles()
      // optimistic check
      ?.first { it.name == "book" && it.isDirectory || it.name == "configure" && it.isDirectory }
      ?.let {
        return AudioPenDevice(TYPE, rootFolder)
      }
  }

  override val type = PenType(NAME, TYPE)

  companion object {
    internal const val TYPE = "bookii"
    internal const val NAME = "Bookii"
    private const val API_BASE_URL = "https://www.bookii-medienservice.de/Medienserver-1.0/api/"
  }
}