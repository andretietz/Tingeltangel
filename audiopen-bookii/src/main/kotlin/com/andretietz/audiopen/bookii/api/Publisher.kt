package com.andretietz.audiopen.bookii.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class Publisher(
  @Json(name = "publisherId")
  val id: Int,
  @Json(name = "name")
  val name: String
)
