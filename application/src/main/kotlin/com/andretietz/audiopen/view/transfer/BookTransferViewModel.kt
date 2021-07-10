package com.andretietz.audiopen.view.transfer

import com.andretietz.audiopen.AudioPenDevice
import com.andretietz.audiopen.BookDisplay

class BookTransferViewModel {
  private var selectedDevice: AudioPenDevice? = null

  fun transferBook(book: BookDisplay) {
    if (selectedDevice != null) {
      println("Transfer: $book to $selectedDevice")
      // TODO transfer to device
    }
  }

  fun selectDevice(device: AudioPenDevice?) {
    selectedDevice = device
  }
}
