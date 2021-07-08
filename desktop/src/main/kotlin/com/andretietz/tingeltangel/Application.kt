package com.andretietz.tingeltangel

import ManagerView
import androidx.compose.ui.Alignment
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.andretietz.audiopen.bookii.BookiiDeviceManager
import com.andretietz.audiopen.bookii.BookiiSource
import com.andretietz.audiopen.ting.TingSource
import com.andretietz.tingeltangel.devicedetector.DummyAudioPenDetector
import com.andretietz.tingeltangel.devicedetector.DummyDeviceManager
import com.andretietz.tingeltangel.devicedetector.USBDriveDetectorAudioPenDetector
import com.andretietz.tingeltangel.ui.TingeltangelTheme
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.io.File
import javax.imageio.ImageIO

class Application {
  companion object {
    @JvmStatic
    fun main(args: Array<String>) {
      Application().run()
    }

    /**
     * Application global coroutine scope.
     */
    val coroutineScope = CoroutineScope(Dispatchers.Default + CoroutineName("TingeltangelMain"))
    private val HOME = System.getProperty("user.home")
    private val CACHE_DIR = File("$HOME/.tingeltangel/cache/")

    private const val CACHE_SIZE = 50L * 1024L * 1024L // 50MB
    private const val CACHE_AGE_TIME = 24
  }

  fun run() = application {
    val windowState = rememberWindowState(
      isOpen = true
    )
    TingeltangelTheme {
      Window(
        windowState,
        title = "Tingeltangel 0.1.0",
        icon = ImageIO.read(this.javaClass.getResourceAsStream("/images/icon.png")),
        initialAlignment = Alignment.Center,
        resizable = false
      ) {
        val deviceManager = listOf(BookiiDeviceManager(), DummyDeviceManager())
        ManagerView(
          coroutineScope,
          listOf(
            TingSource(CACHE_DIR),
            BookiiSource(CACHE_DIR),
          ),
          deviceManager,
          DummyAudioPenDetector(File(CACHE_DIR, "dummydevice"))
//          USBDriveDetectorAudioPenDetector(deviceManager, coroutineScope)
        )
      }
    }
  }
}
