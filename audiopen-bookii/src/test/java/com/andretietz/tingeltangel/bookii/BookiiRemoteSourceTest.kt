package com.andretietz.tingeltangel.bookii

import com.andretietz.audiopen.Thumbnail
import com.andretietz.audiopen.bookii.BookiiSource
import com.andretietz.tingeltangel.bookii.Helper.MOCK_CONFIG_FOLDER
import com.andretietz.tingeltangel.bookii.Helper.mockFileAsResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

@ExperimentalCoroutinesApi
internal class BookiiRemoteSourceTest {

  @Test
  fun `Receiving books`() = runBlocking {
    val server = MockWebServer()
    server.enqueue(mockFileAsResponse("get-ids.json"))
    server.enqueue(mockFileAsResponse("get-book-info-01.json"))
    server.enqueue(mockFileAsResponse("get-book-info-02.json"))

    val books = BookiiSource(
      cacheDir = MOCK_CONFIG_FOLDER,
      baseUrl = server.url("/").toString()
    ).availableBooks()

    assertThat(books.size).isEqualTo(28)
    val book = books.firstOrNull { info -> info.id == "9942" }!!

    assertThat(book.id).isEqualTo("9942")
    assertThat(book.title).isEqualTo("Frühe Sprachbildung - Vorkurs")
    assertThat(book.thumbnail).isInstanceOf(Thumbnail.Remote::class.java)
//      .isEqualTo("http://www.bookii-streamingservice.de/files/3/9942/9942_en.png")
//    assertThat(
//      book.contentUrl
//        .toString()
//    ).isEqualTo("http://www.bookii-streamingservice.de/files/3/9942/3/09942_en.kii")
//    assertThat(book.version).isEqualTo(3)
//    assertThat(book.type.type).isEqualTo("bookii")

    server.shutdown()
  }
}