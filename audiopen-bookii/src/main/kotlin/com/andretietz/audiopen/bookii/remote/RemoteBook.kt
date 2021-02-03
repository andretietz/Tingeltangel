package com.andretietz.audiopen.bookii.remote

import com.andretietz.audiopen.Thumbnail

internal data class RemoteBook(
  val id: String,
  val title: String,
  val thumbnail: Thumbnail
)
