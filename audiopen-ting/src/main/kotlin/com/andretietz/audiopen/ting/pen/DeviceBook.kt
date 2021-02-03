package com.andretietz.audiopen.ting.pen

data class DeviceBook(
  val id: String,
  val title: String,
  val publisher: String,
  val author: String,
  val version: Int,
  val url: String,
  val thumbMD5: String,
  val fileMD5: String,
  val areaCode: String
)
