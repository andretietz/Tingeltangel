package com.andretietz.tingeltangel.pencontract

import java.io.File

interface AudioPenDeviceManager {
  fun verifyDevice(rootFolder: File): Boolean
  suspend fun booksFromDevice(device: AudioPenDevice): List<Book>
}
