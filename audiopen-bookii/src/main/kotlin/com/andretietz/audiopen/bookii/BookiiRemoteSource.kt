package com.andretietz.audiopen.bookii

import com.andretietz.audiopen.BookDisplay
import com.andretietz.audiopen.Thumbnail
import com.andretietz.audiopen.bookii.remote.BookiiApi
import com.andretietz.audiopen.bookii.remote.RemoteBook
import com.andretietz.audiopen.remote.RemoteBookSource
import okhttp3.HttpUrl.Companion.toHttpUrl
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

  override suspend fun availableBooks(): List<BookDisplay> = api.versions().map { it.key }
    .chunked(MAX_BOOKINFO_ITEMS)
    .map { api.info(it.joinToString(",") { item -> "\"$item\"" }) }
    .flatten()
    .map {
      RemoteBook(
        it.mid,
        it.title,
        Thumbnail.Remote(
          IMAGE_URL.format(it.publisher.id, it.mid.toInt(), it.mid.toInt(), it.areaCode)
            .toHttpUrl().toUrl()
        )
      )
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
