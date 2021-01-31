package com.andretietz.tingeltangel.manager

import com.andretietz.audiopen.AudioPenDevice
import com.andretietz.audiopen.BookInfo
import com.andretietz.audiopen.Type
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
  fun selectBookSource(sourceType: Type?)
  fun transferBookToDevice(book: BookInfo?, audioPen: AudioPenDevice?)
}
