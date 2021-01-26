package com.andretietz.tingeltangel.bookii

import com.andretietz.tingeltangel.pencontract.BookInfo
import com.andretietz.tingeltangel.pencontract.BookSource
import com.squareup.moshi.Moshi
import okhttp3.Cache
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import okhttp3.CacheControl

import java.io.IOException

import okhttp3.Interceptor
import okhttp3.Response


class BookiiBookSource : BookSource {

    private val api by lazy {
        Retrofit.Builder()
            .baseUrl("https://www.bookii-medienservice.de/Medienserver-1.0/api/")
            .addConverterFactory(
                MoshiConverterFactory.create(
                    Moshi.Builder()
//                        .add(CategoryAdapter)
//                        .add(ProductAdapter)
//                        .add(StoreAdapter)
                        .build()
                )
            )
            .client(
                OkHttpClient.Builder()
//                    .addInterceptor {
//                        val request = it.request().newBuilder()
//                            .addHeader("Authorization", "Bearer ${configuration.accessToken}")
//                            .build()
//                        it.proceed(request)
//                    }
                    .cache(Cache(
                        directory = File(CONFIG, "http_cache"),
                        // $0.05 worth of phone storage in 2020
                        maxSize = 50L * 1024L * 1024L // 50 MiB
                    ))
                    .addInterceptor(HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
                        override fun log(message: String) = println(message)
                    }).apply { level = HttpLoggingInterceptor.Level.BODY })

                    .addNetworkInterceptor(CacheInterceptor())
                    .readTimeout(60, TimeUnit.SECONDS)
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .build()
            )
            .build().create(BookiiApi::class.java)
    }

    class CacheInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            val response: Response = chain.proceed(chain.request())
            val cacheControl: CacheControl = CacheControl.Builder()
                .maxAge(15, TimeUnit.MINUTES) // 15 minutes cache
                .build()
            return response.newBuilder()
                .removeHeader("Pragma")
                .removeHeader("Cache-Control")
                .header("Cache-Control", cacheControl.toString())
                .build()
        }
    }

    init {
        val config = File(CONFIG)
        if (!config.exists()) {
            config.mkdirs()
        }
    }

    override suspend fun availableBooks() = api.versions().map { it.key }
        .chunked(10)
        .take(1)// TODO tmp
        .map { api.info(it.joinToString(",") { item -> "\"$item\"" }) }
        .flatten()
        .map { info ->
            val imageFile = File("$CONFIG/${info.id}.png")
            if (!imageFile.exists()) {
                // Download image - may be move this to a later point of time...
                MEDIA_URL.format(info.publisher.id, info.id, info.id)
                    .toHttpUrlOrNull()?.toUrl()?.openStream()?.use { inputStream ->
                        imageFile.outputStream().use { outputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }
            }
            return@map BookInfo(
                id = info.id.toString(),
                type = TYPE,
                title = info.title,
                image = if (imageFile.exists()) imageFile else null
            )
        }
        .toSet()

    companion object {
        const val TYPE = "bookii"
        private const val MEDIA_URL = "http://www.bookii-streamingservice.de/files/%d/%d/%d_en.png"

        // TODO:
        private val HOME = System.getProperty("user.home")
        private val CONFIG = "${HOME}/.tingeltangel/cache/$TYPE"
    }
}
