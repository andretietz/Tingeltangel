package com.andretietz.audiopen.localbooks

import com.andretietz.audiopen.BookDisplay
import com.andretietz.audiopen.Type
import com.andretietz.audiopen.data.BookPersister
import com.andretietz.audiopen.remote.BookSource

class LocalBookSource(
  private val bookPersister: BookPersister
) : BookSource {
  override val type = TYPE

  override suspend fun availableBooks(): List<BookDisplay> {
    return bookPersister.load().map { book ->
      BookDisplay(
        book.id.toString(),
        book.title,
        book.thumbnail
      )
    }
  }

  companion object {
    private val TYPE = Type("local", "local")
  }
}
