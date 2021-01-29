package com.andretietz.tingeltangel.manager

import com.andretietz.tingeltangel.pencontract.AudioPenContract
import com.andretietz.tingeltangel.pencontract.AudioPenDetector
import com.andretietz.tingeltangel.pencontract.AudioPenDevice
import com.andretietz.tingeltangel.pencontract.BookInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.File
import java.net.URL

class ManagerViewModel(
  override val scope: CoroutineScope,
  private val imageCache: ImageCache,
  private val sources: List<AudioPenContract>,
  private val deviceDetector: AudioPenDetector
) : Interactor {

  private lateinit var currentlyLoadedLocalBooks: List<BookInfo>
  private val connectedDevices: MutableList<AudioPenDevice> = mutableListOf()

  private val channel = Channel<ViewState>(Channel.CONFLATED).apply {
    scope.launch {
      currentlyLoadedLocalBooks = sources.first().source().availableBooks()
      send(ViewState.Init(
        currentlyLoadedLocalBooks,
        sources.map { it.type }
      ))
    }
  }

  init {
    scope.launch {
      deviceDetector.detect()
        .collect { event ->
          when (event) {
            is AudioPenDetector.DetectorEvent.Connected -> {
              connectedDevices.add(event.device)
              channel.send(ViewState.DeviceListUpdate(connectedDevices))
            }
            is AudioPenDetector.DetectorEvent.Disconnected -> {
              connectedDevices.remove(event.device)
              channel.send(ViewState.DeviceListUpdate(connectedDevices))
            }
          }
        }
    }
  }

  override val state: ReceiveChannel<ViewState> = channel
  override fun loadImage(url: URL, callback: (file: File) -> Unit) {
    imageCache.image(url, callback)
  }

  override fun filterLocalBooks(filter: String?) {
    if (filter == null) {
      scope.launch {
        channel.send(ViewState.LocalBookListUpdate(currentlyLoadedLocalBooks))
      }
    } else {
      scope.launch {
        channel.send(ViewState.LocalBookListUpdate(currentlyLoadedLocalBooks.filter { it.title.contains(filter) }))
      }
    }
  }

  override fun filterDeviceBooks(filter: String?) {
    // TODO
  }
}
