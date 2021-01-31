package com.andretietz.tingeltangel.manager

import com.andretietz.audiopen.AudioPenDetector
import com.andretietz.audiopen.AudioPenDevice
import com.andretietz.audiopen.BookInfo
import com.andretietz.audiopen.Type
import com.andretietz.audiopen.device.DeviceManager
import com.andretietz.audiopen.remote.RemoteBookSource
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
  private val remoteSource: List<RemoteBookSource>,
  private val deviceManager: List<DeviceManager>,
  private val deviceDetector: AudioPenDetector
) : Interactor {

  private lateinit var currentlyLoadedLocalBookInfos: List<BookInfo>
  private lateinit var currentlyLoadedDeviceBookInfos: List<BookInfo>
  private val connectedDevices: MutableList<AudioPenDevice> = mutableListOf()

  private val channel = Channel<ViewState>(Channel.CONFLATED).apply {
    scope.launch {
      currentlyLoadedLocalBookInfos = remoteSource.first().availableBooks()
      send(ViewState.Init(
        remoteSource.map { it.type }
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
        currentlyLoadedDeviceBookInfos = deviceManager
          .first { it.type == device.type }
          .booksFromDevice(device)
          .map { it.info }
        channel.send(ViewState.DeviceBookUpdate(currentlyLoadedDeviceBookInfos))
      }
    }
  }

  override fun selectBookSource(sourceType: Type?) {
    scope.launch {
      if (sourceType == null) {
        channel.send(ViewState.LocalBookListUpdate(emptyList()))
      } else {
        currentlyLoadedLocalBookInfos = remoteSource.first { it.type == sourceType }.availableBooks()
        channel.send(ViewState.LocalBookListUpdate(currentlyLoadedLocalBookInfos))
      }
    }
  }

  override fun transferBookToDevice(book: BookInfo?, audioPen: AudioPenDevice?) {
    if (book == null || audioPen == null) return
    println("TBD: transfer $book to $audioPen")
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
