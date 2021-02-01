package com.andretietz.audiopen.ting.pen

import com.andretietz.audiopen.BookDisplay
import com.andretietz.audiopen.Thumbnail
import java.net.URL

data class DeviceBook(
  override val id: String,
  override val title: String,
  val publisher: String,
  val author: String,
  val version: Int,
  val url: String,
  val thumbMD5: String,
  val fileMD5: String,
  val areaCode: String,
  private val thumbUrl: URL
) : BookDisplay {
  override val thumbnail = Thumbnail.Remote(thumbUrl)
}
