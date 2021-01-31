package com.andretietz.audiopen.bookii.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class BookResponse(
  val mediaId: Int,
  val mid: String,
  val title: String,
  val author: String,
  val thumbMD5: String,
  val fileMD5: String,
  @Json(name = "bookAreaCode")
  val areaCode: String,
  val isbn: String,
  val ean: String,
  val publisherDate: Long?,
  val lastModificationDate: Long?,
  val articleNo: String,
  val volume: Int,
  val shortDescription: String?,
  val description: String?,
  val oidsPerUnit: Int,
  val coverUrl: String?,
  val mediaType: String,
  val mediaStatus: String,
  val versionCount: Int,
  val publisher: Publisher,
  val global: Boolean
)
