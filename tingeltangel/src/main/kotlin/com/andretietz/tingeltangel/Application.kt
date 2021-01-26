package com.andretietz.tingeltangel

import com.andretietz.tingeltangel.bookii.BookiiContract
import com.andretietz.tingeltangel.cache.CacheInterceptor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File
import java.util.concurrent.TimeUnit

class Application {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Application().run()
        }

        val coroutineScope = CoroutineScope(Dispatchers.IO)

        // TODO:
        private val HOME = System.getProperty("user.home")
        private val CONFIG = "${HOME}/.tingeltangel/cache/"

        private const val CACHE_SIZE = 50L * 1024L * 1024L // 50MB
        private const val CACHE_AGE_TIME = 24

    }

    private val client = OkHttpClient.Builder()
        .cache(Cache(File(CONFIG, "http_cache"), CACHE_SIZE))
        .addInterceptor(HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) = println(message)
        }).apply { level = HttpLoggingInterceptor.Level.BODY })
        .addNetworkInterceptor(CacheInterceptor(CACHE_AGE_TIME, TimeUnit.HOURS))
        .build()

    fun run() {
//        launch<MainApp>()
        val contract = BookiiContract(client, File(CONFIG))

        runBlocking {
            async(coroutineContext) {
                contract.source().availableBooks().firstOrNull()?.let {
                    println(contract.source().getBook(it))
                }
            }
        }

    }
}


