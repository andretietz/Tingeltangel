package com.andretietz.tingeltangel.manager

import com.andretietz.tingeltangel.pencontract.AudioPenContract
import com.andretietz.tingeltangel.pencontract.AudioPenDevice
import com.andretietz.tingeltangel.pencontract.BookInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ReceiveChannel
import java.io.File
import java.net.URL

interface Interactor {
  /**
   * Coroutine Scope.
   */
  val scope: CoroutineScope

  /**
   * channel for listenings to the viewstate.
   */
  val state: ReceiveChannel<ViewState>

  fun loadImage(url: URL, callback: (file: File) -> Unit)

  fun filterLocalBooks(filter: String?)
  fun filterDeviceBooks(filter: String?)

  fun selectAudioPen(device: AudioPenDevice?)
  fun removeFromDevice(book: BookInfo)
  fun selectBookSource(sourceType: AudioPenContract.Type?)
}
