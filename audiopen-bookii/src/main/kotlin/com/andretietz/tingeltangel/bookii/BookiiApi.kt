package com.andretietz.tingeltangel.bookii

import com.andretietz.tingeltangel.bookii.model.Info
import retrofit2.http.GET
import retrofit2.http.Query

internal interface BookiiApi {

    @GET("download/versions")
    suspend fun versions(): Map<String, Int>

    @GET("download/medias")
    suspend fun info(@Query("mids", encoded = true) ids: String): List<Info>

    @GET("download/fileSize")
    suspend fun fileSize(@Query("mid", encoded = true) id: String, @Query("version") version: Int): Long
}
