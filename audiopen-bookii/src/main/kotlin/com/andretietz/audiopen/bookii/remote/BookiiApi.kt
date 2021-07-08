package com.andretietz.audiopen.bookii.remote

import com.andretietz.retrofit.ResponseCache
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

internal interface BookiiApi {

  @ResponseCache(value = 7, unit = TimeUnit.DAYS, override = true)
  @GET("download/versions")
  suspend fun versions(): Map<String, Int>

  @ResponseCache(value = 7, unit = TimeUnit.DAYS, override = true)
  @GET("download/medias")
  suspend fun info(@Query("mids", encoded = true) ids: String): List<BookResponse>

  @GET("download/fileSize")
  suspend fun fileSize(@Query("mid", encoded = true) id: String, @Query("version") version: Int): Long
}
