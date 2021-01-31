package com.andretietz.audiopen.bookii.pen

import com.andretietz.audiopen.BookDisplay
import com.andretietz.audiopen.Thumbnail
import java.io.File

internal data class DeviceBook(
  override val id: String,
  override val title: String,
  val publisher: String,
  val author: String,
  val version: Int,
  val url: String,
  val thumbMD5: String,
  val fileMD5: String,
  val areaCode: String,
  val type: String,
  val isbn: String,
  val volume: Int,
  internal val thumbnailFile: File,
  internal val infoFile: File,
  internal val dataFile: File
) : BookDisplay {
  override val thumbnail = Thumbnail.Local(thumbnailFile)
}
