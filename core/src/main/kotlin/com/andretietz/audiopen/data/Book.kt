package com.andretietz.audiopen.data

import com.andretietz.audiopen.Thumbnail

data class Book(
  val id: Int,
  val title: String,
  val thumbnail: Thumbnail,
  val data: List<BookItem>,
  val version: Int = 1,
  val publisher: String = "",
  val author: String = ""
)
