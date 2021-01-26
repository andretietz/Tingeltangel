package com.andretietz.tingeltangel.bookii.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Info(
    @Json(name = "mid")
    val id: Int,
    val title: String,

    /**
     * This is the bookAreaCode, which for some reason is mainly "en".
     */
    @Json(name = "bookAreaCode")
    val areaCode: String,
    val publisher: Publisher
)

@JsonClass(generateAdapter = true)
data class Publisher(
    @Json(name = "publisherId")
    val id: Int
)
