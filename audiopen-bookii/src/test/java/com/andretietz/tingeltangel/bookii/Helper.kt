package com.andretietz.tingeltangel.bookii

import okhttp3.HttpUrl
import okhttp3.mockwebserver.MockResponse
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File

object Helper {

    internal fun createApi(baseUrl: HttpUrl): BookiiApi {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(MoshiConverterFactory.create())
            .build().create(BookiiApi::class.java)
    }

    internal fun mockFileAsResponse(file: String) = MockResponse()
        .setBody(File("src/test/resources/mockresponses", file).readText())

    val MOCK_CONFIG_FOLDER = File("src/test/resources/cache/bookii")
}
