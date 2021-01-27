package com.andretietz.tingeltangel

import com.andretietz.tingeltangel.bookii.BookiiContract
import com.andretietz.tingeltangel.cache.HttpCacheInterceptor
import com.andretietz.tingeltangel.manager.Interactor
import com.andretietz.tingeltangel.manager.ManagerViewModel
import com.andretietz.tingeltangel.pencontract.BookSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import tornadofx.launch
import java.io.File
import java.util.concurrent.TimeUnit

class Application {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Application().run()
        }

        val coroutineScope = CoroutineScope(Dispatchers.IO)
        private val HOME = System.getProperty("user.home")
        private val CONFIG = "$HOME/.tingeltangel/cache/"

        private const val CACHE_SIZE = 50L * 1024L * 1024L // 50MB
        private const val CACHE_AGE_TIME = 24
    }

    init {
        DIHelper.initKodein(Kodein {

            bind<File>() with singleton { File(CONFIG) }

            bind<OkHttpClient>() with singleton {
                OkHttpClient.Builder()
                    .cache(Cache(File(instance<File>(), "http_cache"), CACHE_SIZE))
                    .addInterceptor(HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
                        override fun log(message: String) = println(message)
                    }).apply { level = HttpLoggingInterceptor.Level.BODY })
                    .addNetworkInterceptor(HttpCacheInterceptor(CACHE_AGE_TIME, TimeUnit.HOURS))
                    .build()
            }

            bind<List<BookSource>>() with singleton {
                listOf(BookiiContract(instance(), instance()).source())
            }

            bind<Interactor>() with singleton {
                ManagerViewModel(coroutineScope, instance())
            }
        })
    }

    fun run() {
        launch<MainApp>()
//        val contract = BookiiContract(client, File(CONFIG))
//        USBDeviceDetectorManager().addDriveListener {
//            runBlocking {
//                println(contract.verifyDevice(it.storageDevice.rootDirectory))
//            }
//        }
//
//        Thread.sleep(10000)
    }
}
