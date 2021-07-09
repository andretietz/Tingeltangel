package com.andretietz.audiopen.bookii.pen

import java.io.File

internal data class DeviceBook(
  val id: String,
  val title: String,
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
  val thumbnail: File
)
