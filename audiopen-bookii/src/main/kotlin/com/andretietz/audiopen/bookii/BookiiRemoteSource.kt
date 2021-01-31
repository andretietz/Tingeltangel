package com.andretietz.audiopen.bookii

import com.andretietz.audiopen.Book
import com.andretietz.audiopen.BookInfo
import com.andretietz.audiopen.bookii.api.BookiiApi
import com.andretietz.audiopen.remote.RemoteBookSource
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File

@SuppressWarnings("Detekt.UnusedPrivateMember")
class BookiiRemoteSource(
  private val cacheDir: File,
  httpClient: OkHttpClient = OkHttpClient(),
  baseUrl: String = Bookii.API_BASE_URL
) : RemoteBookSource {
  override val type = Bookii.AUDIOPEN_TYPE

  private val api by lazy {
    Retrofit.Builder()
      .baseUrl(baseUrl)
      .addConverterFactory(MoshiConverterFactory.create())
      .client(httpClient)
      .build().create(BookiiApi::class.java)
  }

  override suspend fun availableBooks() = api.versions().map { it.key }
    .chunked(MAX_BOOKINFO_ITEMS)
    .map { api.info(it.joinToString(",") { item -> "\"$item\"" }) }
    .flatten()
    .map { info ->
      BookInfo(
        id = info.id.toString(),
        type = Bookii.AUDIOPEN_TYPE,
        title = info.title,
        version = info.version,
        image = IMAGE_URL.format(info.publisher.id, info.id, info.id, info.areaCode).toHttpUrlOrNull()?.toUrl(),
        contentUrl = BOOK_URL.format(
          info.publisher.id,
          info.id,
          info.version,
          info.id.toString().padStart(BOOK_ID_PAD, '0'),
          info.areaCode
        ).toHttpUrl().toUrl(),
        areaCode = info.areaCode,
        publisherName = info.publisher.name,
        authorName = info.author,
        mediaType = info.mediaType,
        volume = info.volume,
        isbn = info.isbn
      )
    }

  override suspend fun getBook(bookInfo: BookInfo): Book {
    val size = api.fileSize(bookInfo.id, bookInfo.version)
    println(size)
    return Book(bookInfo)
  }

  companion object {
    // publisherId, bookid, bookid, areaCode
    private const val IMAGE_URL = "http://www.bookii-streamingservice.de/files/%d/%d/%d_%s.png"

    // publisherId, bookid, version, bookid (5 chars!), areaCode
    private const val BOOK_URL = "http://www.bookii-streamingservice.de/files/%d/%d/%d/%s_%s.kii"

    private const val MAX_BOOKINFO_ITEMS = 20
    private const val BOOK_ID_PAD = 5
  }
}
