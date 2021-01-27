package com.andretietz.tingeltangel.bookii

import com.andretietz.tingeltangel.bookii.Helper.MOCK_CONFIG_FOLDER
import com.andretietz.tingeltangel.bookii.Helper.createApi
import com.andretietz.tingeltangel.bookii.Helper.mockFileAsResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

@ExperimentalCoroutinesApi
internal class BookiiBookSourceTest {

    @Test
    fun `Receiving books`() = runBlocking {
        val server = MockWebServer()
        val api = createApi(server.url("/"))
        server.enqueue(mockFileAsResponse("get-ids.json"))
        server.enqueue(mockFileAsResponse("get-book-info.json"))

        val books = BookiiBookSource(api, MOCK_CONFIG_FOLDER).availableBooks()

        assertThat(books.size).isEqualTo(3)

        assertThat(books[0].id).isEqualTo("9942")
        assertThat(books[0].title).isEqualTo("Frühe Sprachbildung - Vorkurs")
        assertThat(books[0].image.toString())
            .isEqualTo("http://www.bookii-streamingservice.de/files/3/9942/9942_en.png")
        assertThat(books[0].contentUrl
            .toString()).isEqualTo("http://www.bookii-streamingservice.de/files/3/9942/3/09942_en.kii")
        assertThat(books[0].version).isEqualTo(3)
        assertThat(books[0].type).isEqualTo("bookii")

        server.shutdown()
    }
}
