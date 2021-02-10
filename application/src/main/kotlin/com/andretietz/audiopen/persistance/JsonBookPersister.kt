package com.andretietz.audiopen.persistance

import com.andretietz.audiopen.LoggerDelegate
import com.andretietz.audiopen.data.Book
import com.andretietz.audiopen.data.BookPersister
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Persists local books
 */
class JsonBookPersister(
  private val cacheDir: File
) : BookPersister {


  private val moshi by lazy {
    Moshi.Builder()
      .add(
        PolymorphicJsonAdapterFactory.of(JsonBookDataItem::class.java, "type")
          .withSubtype(JsonBookDataItem.JsonMP3::class.java, "audio")
          .withSubtype(JsonBookDataItem.JsonScript::class.java, "script")
      )
      .build()
  }


  override suspend fun load(): List<Book> = cacheDir.listFiles()?.let { files ->
    files.filter { it.exists() && it.isDirectory && it.name.matches(BOOK_ID_REGEX) }
      .map { File(it, "${it.name}.json") }
      .filter { it.exists() && it.isFile && it.canRead() }
      .mapNotNull { moshi.adapter(JsonBookData::class.java).fromJson(it.readText())?.toBook() }
  } ?: emptyList()


  override suspend fun load(id: Int): Book? {
    val name = id.toString().padStart(5, '0')
    val file = File(cacheDir, "$name/$name.json")
    if (!file.exists() || !file.isFile) return null
    return withContext(Dispatchers.IO) {
      moshi.adapter(JsonBookData::class.java).fromJson(file.readText())?.toBook()
    }
  }

  override suspend fun persist(book: Book) {
    val name = book.id.toString().padStart(5, '0')
    val bookDirectory = File(cacheDir, name)
      .also { if (!it.exists()) it.mkdirs() }
    val jsonFileName = File(bookDirectory, "$name.json")
    val content = moshi.adapter(JsonBookData::class.java).toJson(JsonBookData.from(book))
    logger.debug("Content: $content")
    jsonFileName.writeText(content)
  }

  companion object {
    private val BOOK_ID_REGEX = "^[0-9]{5}$".toRegex()
    private val logger by LoggerDelegate()
  }
}
