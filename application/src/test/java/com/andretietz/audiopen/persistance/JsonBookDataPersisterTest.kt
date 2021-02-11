package com.andretietz.audiopen.persistance

import com.andretietz.audiopen.data.BookData
import com.andretietz.audiopen.data.BookItem
import com.andretietz.audiopen.data.BookPersister
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

@ExperimentalCoroutinesApi
class JsonBookDataPersisterTest {

  @get:Rule
  val cacheDir = TemporaryFolder()

  @Test
  fun `persist a book`() = runBlockingTest {
    val folder = cacheDir.newFolder("cache")
    val persister: BookPersister = JsonBookPersister(folder)
    val bookToStore = BookData(
      1234,
      setOf(
        BookItem.MP3(
          15001,
          File("src/test/resources/testinput/15001.mp3")
        )
      )
    )
    persister.persist(bookToStore)

    val files = folder.listFiles()
    assertThat(files).isNotNull
    val contentFolder = File(folder, "01234")
    assertThat(files).contains(contentFolder)
    val bookFile = File(contentFolder, "01234.json")
    assertThat(bookFile).exists()
    // TBD: check content?
  }

  @Test
  fun `load a book`(): Unit = runBlocking {
//    withContext(Dispatchers.IO) {
    val directory = cacheDir.newFolder("cache")
    // push demo book json
    val bookDir = File(directory, "01234").also { it.mkdirs() }
    val bookFile = File(bookDir, "01234.json").also { it.createNewFile() }
    bookFile.writeText(DEMO_BOOK)


    val persister: BookPersister = JsonBookPersister(directory)
    val book = persister.load(1234)

    assertThat(book).isNotNull
    assertThat(book!!.id).isEqualTo(1234)
    assertThat(book.data.size).isEqualTo(1)
    assertThat(book.data.firstOrNull()).isNotNull
    assertThat(book.data.first()).isInstanceOf(BookItem.MP3::class.java)
    assertThat(book.data.first()).isInstanceOf(BookItem.MP3::class.java)
    assertThat(book.data.first().code).isEqualTo(15001)
    assertThat((book.data.first() as BookItem.MP3).file.invariantSeparatorsPath).isEqualTo("testinput/15001.mp3")

    cacheDir.delete()
  }

  companion object {
    const val DEMO_BOOK =
      "{\"id\":1234,\"items\":[{\"type\":\"audio\",\"code\":15001,\"file\":\"testinput/15001.mp3\",\"corrupted\":false}]}"
  }
}
