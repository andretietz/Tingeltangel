package com.andretietz.audiopen.data

data class BookData(
  val id: Int,
  val data: List<BookDataItem>
) {
  fun getItem(id: Int) = data.firstOrNull { it.code == id }
}
