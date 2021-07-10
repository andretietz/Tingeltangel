package com.andretietz.audiopen.local

import com.andretietz.audiopen.AudioPenDevice
import com.andretietz.audiopen.BookDisplay
import com.andretietz.audiopen.device.DeviceManager
import java.io.File

class LocalStorageManager(
  private val localDir: File
) : DeviceManager {
  override val type = LocalStore.AUDIOPEN_TYPE

  override fun verifyDevice(rootDir: File): Boolean {
    return true
  }

  override suspend fun booksFromDevice(device: AudioPenDevice): List<BookDisplay> {
    return emptyList()
  }

  override suspend fun transfer(book: BookDisplay, device: AudioPenDevice) {
    TODO("Not yet implemented")
  }
}