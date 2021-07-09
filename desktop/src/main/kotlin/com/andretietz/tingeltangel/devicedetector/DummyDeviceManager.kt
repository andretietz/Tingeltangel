package com.andretietz.tingeltangel.devicedetector

import com.andretietz.audiopen.AudioPenDevice
import com.andretietz.audiopen.BookDisplay
import com.andretietz.audiopen.Type
import com.andretietz.audiopen.device.DeviceManager
import java.io.File

class DummyDeviceManager : DeviceManager {
  override val type = Type("dummy", "dummy")

  private val deviceBookList = mutableListOf<BookDisplay>()

  override fun verifyDevice(rootDir: File) = true

  override suspend fun booksFromDevice(device: AudioPenDevice): List<BookDisplay> = deviceBookList
  override suspend fun transfer(book: BookDisplay, device: AudioPenDevice) {
    TODO("Not yet implemented")
  }
}
