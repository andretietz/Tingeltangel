package com.andretietz.audiopen.data

interface BookPersister {
  suspend fun load(): List<Book>
  suspend fun persist(book: Book)
  suspend fun load(id: Int): Book?
}
