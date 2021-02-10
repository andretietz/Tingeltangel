package com.andretietz.audiopen.persistance

import com.andretietz.audiopen.data.Book
import com.andretietz.audiopen.data.BookItem
import com.squareup.moshi.JsonClass
import java.io.File


@JsonClass(generateAdapter = true)
internal data class JsonBookData(val id: Int, val items: Set<JsonBookDataItem>) {
  companion object {
    fun from(book: Book) = JsonBookData(
      book.id,
      book.data.map {
        when (it) {
          is BookItem.MP3 -> JsonBookDataItem.JsonMP3.from(it)
          is BookItem.Script -> JsonBookDataItem.JsonScript.from(it)
        }
      }.toSet()
    )
  }

  fun toBook() = Book(
    id,
    items.map {
      when (it) {
        is JsonBookDataItem.JsonMP3 -> it.toMP3()
        is JsonBookDataItem.JsonScript -> it.toScript()
        else -> throw IllegalArgumentException("Cannot parse book item of type: ${it::class.java}")
      }
    }.toSet()
  )

}

@JsonClass(generateAdapter = true)
internal open class JsonBookDataItem( // TBD: why is sealed not working?
  open val code: Int
) {
  @JsonClass(generateAdapter = true)
  internal data class JsonMP3(
    override val code: Int,
    val file: String,
    val corrupted: Boolean = false
  ) : JsonBookDataItem(code) {
    fun toMP3() = BookItem.MP3(code, File(file), corrupted)

    companion object {
      fun from(item: BookItem.MP3) = JsonMP3(item.code, item.file.canonicalPath, item.corrupted)
    }
  }

  @JsonClass(generateAdapter = true)
  internal data class JsonScript(
    override val code: Int,
    val script: String,
    val isSubRoutine: Boolean
  ) : JsonBookDataItem(code) {
    fun toScript() = BookItem.Script(code, script, isSubRoutine)

    companion object {
      fun from(item: BookItem.Script) = JsonScript(item.code, item.script, item.isSubRoutine)
    }
  }
}
