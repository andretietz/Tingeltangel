package com.andretietz.audiopen.bookii.remote

import retrofit2.http.GET
import retrofit2.http.Query

internal interface BookiiApi {

  @GET("download/versions")
  suspend fun versions(): Map<String, Int>

  @GET("download/medias")
  suspend fun info(@Query("mids", encoded = true) ids: String): List<BookResponse>

  @GET("download/fileSize")
  suspend fun fileSize(@Query("mid", encoded = true) id: String, @Query("version") version: Int): Long
}
