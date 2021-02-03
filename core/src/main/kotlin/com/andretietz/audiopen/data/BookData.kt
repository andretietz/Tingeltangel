package com.andretietz.audiopen.data

data class BookData(
  val id: Int,
  val data: List<BookDataItem>
) {
  fun getItemByCode(id: Int) = data.firstOrNull { it.code == id }
}
