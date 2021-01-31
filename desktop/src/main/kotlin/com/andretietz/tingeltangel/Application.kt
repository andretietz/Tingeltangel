package com.andretietz.tingeltangel

import com.andretietz.audiopen.AudioPenDetector
import com.andretietz.audiopen.bookii.BookiiDeviceManager
import com.andretietz.audiopen.bookii.BookiiRemoteSource
import com.andretietz.audiopen.device.DeviceManager
import com.andretietz.audiopen.remote.RemoteBookSource
import com.andretietz.tingeltangel.cache.HttpCacheInterceptor
import com.andretietz.tingeltangel.devicedetector.WindowsAudioPenDetector
import com.andretietz.tingeltangel.manager.ImageCache
import com.andretietz.tingeltangel.manager.Interactor
import com.andretietz.tingeltangel.manager.ManagerViewModel
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

    /**
     * Application global coroutine scope.
     */
    val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val HOME = System.getProperty("user.home")
    private val CACHE_DIR = File("$HOME/.tingeltangel/cache/")

    private const val CACHE_SIZE = 50L * 1024L * 1024L // 50MB
    private const val CACHE_AGE_TIME = 24
  }

  init {
    DIHelper.initKodein(Kodein {

      bind<OkHttpClient>() with singleton {
        OkHttpClient.Builder()
          .cache(Cache(File(CACHE_DIR, "http_cache"), CACHE_SIZE))
          .addInterceptor(HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) = println(message)
          }).apply { level = HttpLoggingInterceptor.Level.BODY })
          .addNetworkInterceptor(HttpCacheInterceptor(CACHE_AGE_TIME, TimeUnit.HOURS))
          .build()
      }

      bind<List<RemoteBookSource>>() with singleton {
        listOf(
          BookiiRemoteSource(File(CACHE_DIR, "bookii"), instance())
        )
      }
      bind<List<DeviceManager>>() with singleton {
        listOf(
          BookiiDeviceManager()
        )
      }

      bind<ImageCache>() with singleton {
        ImageCache(File(CACHE_DIR, "images"), coroutineScope)
      }

      bind<Interactor>() with singleton {
        ManagerViewModel(coroutineScope, instance(), instance(), instance(), instance())
      }

      bind<AudioPenDetector>() with singleton {
        WindowsAudioPenDetector(
          instance(), coroutineScope
        )
      }
    })
  }

  fun run() = launch<MainApp>()
}
