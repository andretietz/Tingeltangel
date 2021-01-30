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

  private lateinit var currentlyLoadedLocalBookInfos: List<BookInfo>
  private lateinit var currentlyLoadedDeviceBookInfos: List<BookInfo>
  private val connectedDevices: MutableList<AudioPenDevice> = mutableListOf()

  private val channel = Channel<ViewState>(Channel.CONFLATED).apply {
    scope.launch {
      currentlyLoadedLocalBookInfos = sources.first().source().availableBooks()
      send(ViewState.Init(
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

  override fun loadImage(url: URL, callback: (file: File) -> Unit) = imageCache.image(url, callback)

  override fun selectAudioPen(device: AudioPenDevice?) {
    scope.launch {
      if (device == null) {
        channel.send(ViewState.DeviceBookUpdate(emptyList()))
      } else {
        currentlyLoadedDeviceBookInfos = sources
          .first { it.type == device.type }
          .booksFromDevice(device)
          .map { it.info }
        channel.send(ViewState.DeviceBookUpdate(currentlyLoadedDeviceBookInfos))
      }
    }
  }

  override fun selectBookSource(sourceType: AudioPenContract.Type?) {
    scope.launch {
      if (sourceType == null) {
        channel.send(ViewState.LocalBookListUpdate(emptyList()))
      } else {
        currentlyLoadedLocalBookInfos = sources.first { it.type == sourceType }
          .source().availableBooks()
        channel.send(ViewState.LocalBookListUpdate(currentlyLoadedLocalBookInfos))
      }
    }
  }

  override fun removeFromDevice(book: BookInfo) {
    println("delete: $book")
  }

  override fun filterLocalBooks(filter: String?) {
    scope.launch {
      channel.send(ViewState.LocalBookListUpdate(filter(filter, currentlyLoadedLocalBookInfos)))
    }
  }

  override fun filterDeviceBooks(filter: String?) {
    scope.launch {
      channel.send(ViewState.DeviceBookUpdate(filter(filter, currentlyLoadedDeviceBookInfos)))
    }
  }

  private fun filter(filter: String?, books: List<BookInfo>): List<BookInfo> {
    return if (filter == null) {
      books
    } else {
      books.filter { it.title.contains(filter) || it.id.contains(filter) }
    }
  }
}
