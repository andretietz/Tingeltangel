package com.andretietz.audiopen.persistance

import com.andretietz.audiopen.Thumbnail
import com.andretietz.audiopen.data.Book
import com.andretietz.audiopen.data.BookData
import com.andretietz.audiopen.data.BookItem
import com.squareup.moshi.JsonClass
import dev.zacsweers.moshix.sealed.annotations.TypeLabel
import java.io.File
import java.net.URL

@JsonClass(generateAdapter = true)
internal data class JsonBookData(
  val id: Int,
  val title: String,
  val thumbnail: JsonThumbnail,
  val items: Set<JsonBookDataItem>
) {
  companion object {
    fun from(book: Book) = JsonBookData(
      book.id,
      book.title,
      JsonThumbnail.from(book.thumbnail),
      book.data.data.map {
        when (it) {
          is BookItem.MP3 -> JsonBookDataItem.JsonMP3.from(it)
          is BookItem.Script -> JsonBookDataItem.JsonScript.from(it)
        }
      }.toSet()
    )
  }

  fun toBook() = Book(
    title,
    thumbnail.to(),
    BookData(
      id,
      items.map { it.to() }.toSet()
    )
  )
}

@JsonClass(generateAdapter = true, generator = "sealed:type")
internal sealed class JsonThumbnail {
  @TypeLabel("local")
  @JsonClass(generateAdapter = true)
  internal data class Local(val path: String) : JsonThumbnail() {
    override fun to() = Thumbnail.Local(File(path))
  }

  @TypeLabel("remote")
  @JsonClass(generateAdapter = true)
  internal data class Remote(val url: String) : JsonThumbnail() {
    override fun to() = Thumbnail.Remote(URL(url))
  }

  abstract fun to(): Thumbnail

  companion object {
    fun from(thumbnail: Thumbnail): JsonThumbnail {
      return when (thumbnail) {
        is Thumbnail.Local -> Local(thumbnail.file.path)
        is Thumbnail.Remote -> Remote(thumbnail.url.toString())
        else -> throw IllegalArgumentException("Cannot parse book item of type: ${thumbnail::class.java}")
      }
    }
  }
}

@JsonClass(generateAdapter = true, generator = "sealed:type")
internal sealed class JsonBookDataItem( // TBD: why is sealed not working?
  open val code: Int
) {
  @TypeLabel("audio")
  @JsonClass(generateAdapter = true)
  internal data class JsonMP3(
    override val code: Int,
    val file: String,
    val corrupted: Boolean = false
  ) : JsonBookDataItem(code) {
    override fun to() = BookItem.MP3(code, File(file), corrupted)

    companion object {
      fun from(item: BookItem.MP3) = JsonMP3(item.code, item.file.path, item.corrupted)
    }
  }

  @TypeLabel("script")
  @JsonClass(generateAdapter = true)
  internal data class JsonScript(
    override val code: Int,
    val script: String,
    val isSubRoutine: Boolean
  ) : JsonBookDataItem(code) {
    override fun to() = BookItem.Script(code, script, isSubRoutine)

    companion object {
      fun from(item: BookItem.Script) = JsonScript(item.code, item.script, item.isSubRoutine)
    }
  }
  abstract fun to(): BookItem
}
