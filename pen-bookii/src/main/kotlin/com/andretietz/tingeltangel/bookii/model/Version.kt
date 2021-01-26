package com.andretietz.tingeltangel.bookii.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Version(
    val id: String,
    val version: Int
)
