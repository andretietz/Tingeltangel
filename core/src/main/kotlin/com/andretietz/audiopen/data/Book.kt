package com.andretietz.audiopen.data

import com.andretietz.audiopen.Thumbnail

data class Book(
  val title: String,
  val thumbnail: Thumbnail,
  val data: BookData
) {
  val id = data.id
}
