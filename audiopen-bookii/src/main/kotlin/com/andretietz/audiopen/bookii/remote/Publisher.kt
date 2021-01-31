package com.andretietz.audiopen.bookii.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class Publisher(
  @Json(name = "publisherId")
  val id: Int,
  val name: String,
  val contact: String,
  val ratemodel: String,
  val maxVideoSize: Long
)
