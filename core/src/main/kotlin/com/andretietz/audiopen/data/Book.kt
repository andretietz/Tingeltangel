package com.andretietz.audiopen.data

data class Book(
  val id: Int,
  val data: Set<BookItem>
) {
  fun getItemByCode(id: Int) = data.firstOrNull { it.code == id }
}
