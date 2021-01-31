package com.andretietz.audiopen.view.devices

import com.andretietz.audiopen.AudioPenDetector
import com.andretietz.audiopen.AudioPenDevice
import com.andretietz.audiopen.BookDisplay
import com.andretietz.audiopen.device.DeviceManager
import com.andretietz.audiopen.view.BaseViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class DeviceListViewModel(
  override val scope: CoroutineScope,
  private val deviceManager: List<DeviceManager>,
  private val deviceDetector: AudioPenDetector
) : BaseViewModel<DeviceListViewState>(scope), DeviceListInteractor {

  private lateinit var currentlyLoadedDeviceBookInfos: List<BookDisplay>
  private val connectedDevices: MutableList<AudioPenDevice> = mutableListOf()

  init {
    scope.launch {
      deviceDetector.detect()
        .collect { event ->
          when (event) {
            is AudioPenDetector.DetectorEvent.Connected -> {
              connectedDevices.add(event.device)
              channel.send(DeviceListViewState.DeviceListUpdate(connectedDevices))
            }
            is AudioPenDetector.DetectorEvent.Disconnected -> {
              connectedDevices.remove(event.device)
              channel.send(DeviceListViewState.DeviceListUpdate(connectedDevices))
            }
          }
        }
    }
  }

  override fun selectAudioPen(device: AudioPenDevice?) {
    scope.launch {
      if (device == null) {
        channel.send(DeviceListViewState.DeviceBookUpdate(emptyList()))
      } else {
        currentlyLoadedDeviceBookInfos = deviceManager
          .first { it.type == device.type }
          .booksFromDevice(device)
        channel.send(DeviceListViewState.DeviceBookUpdate(currentlyLoadedDeviceBookInfos))
      }
    }
  }

  override fun filterDeviceBooks(filter: String?) {
    scope.launch {
      channel.send(DeviceListViewState.DeviceBookUpdate(filter(filter, currentlyLoadedDeviceBookInfos)))
    }
  }

  private fun filter(filter: String?, books: List<BookDisplay>): List<BookDisplay> {
    return if (filter == null) {
      books
    } else {
      books.filter { it.title.contains(filter) || it.id.contains(filter) }
    }
  }
}
