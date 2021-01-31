package com.andretietz.audiopen.bookii.remote

import com.andretietz.audiopen.BookDisplay
import com.andretietz.audiopen.Thumbnail

internal data class RemoteBook(
  override val id: String,
  override val title: String,
  override val thumbnail: Thumbnail
) : BookDisplay
