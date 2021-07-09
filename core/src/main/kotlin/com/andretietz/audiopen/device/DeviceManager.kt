package com.andretietz.audiopen.device

import com.andretietz.audiopen.AudioPenDevice
import com.andretietz.audiopen.BookDisplay
import com.andretietz.audiopen.Type
import java.io.File

/**
 * Every audiobook is different in how to read the books out of it and how to write them onto it.
 * This abstraction should hide the details.
 */
interface DeviceManager {
  /**
   * Type of audio-pen.
   */
  val type: Type

  /**
   * @return [true] if [rootDir] is the root of the device of this kind.
   */
  fun verifyDevice(rootDir: File): Boolean

  /**
   * @return all books, located on this device.
   */
  suspend fun booksFromDevice(device: AudioPenDevice): List<BookDisplay>

  /**
   *
   */
  suspend fun transfer(book: BookDisplay, device: AudioPenDevice)
}
