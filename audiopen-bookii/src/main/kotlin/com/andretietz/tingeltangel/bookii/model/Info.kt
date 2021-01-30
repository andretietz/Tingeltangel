package com.andretietz.tingeltangel.bookii.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class Info(
    @Json(name = "mid")
    val id: Int,
    val title: String,

    /**
     * This is the bookAreaCode, which for some reason is mainly "en".
     */
    @Json(name = "bookAreaCode")
    val areaCode: String,
    @Json(name = "versionCount")
    val version: Int,
    val publisher: Publisher,

    val author: String,
    val mediaType: String,
    val volume: Int,
    val isbn: String?

)

@JsonClass(generateAdapter = true)
internal data class Publisher(
    @Json(name = "publisherId")
    val id: Int,
    val name: String
)
