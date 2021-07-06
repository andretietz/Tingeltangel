package com.andretietz.audiopen.view.devices

import com.andretietz.audiopen.AudioPenDetector
import com.andretietz.audiopen.AudioPenDevice
import com.andretietz.audiopen.BookDisplay
import com.andretietz.audiopen.device.DeviceManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class DeviceListViewModel(
  private val scope: CoroutineScope,
  private val deviceManager: List<DeviceManager>,
  private val deviceDetector: AudioPenDetector
) {

  private val _state = MutableStateFlow<DeviceListViewState>(DeviceListViewState.Loading)

  /**
   * channel for listenings to the viewstate.
   */
  val state: StateFlow<DeviceListViewState> = _state

  private lateinit var currentlyLoadedDeviceBookInfos: List<BookDisplay>
  private val connectedDevices: MutableList<AudioPenDevice> = mutableListOf()

  init {
    scope.launch {
      deviceDetector.detect()
        .collect { event ->
          when (event) {
            is AudioPenDetector.DetectorEvent.Connected -> {
              connectedDevices.add(event.device)
              _state.value = DeviceListViewState.DeviceListUpdate(connectedDevices)
            }
            is AudioPenDetector.DetectorEvent.Disconnected -> {
              connectedDevices.remove(event.device)
              _state.value = DeviceListViewState.DeviceListUpdate(connectedDevices)
            }
          }
        }
    }
  }

  fun selectAudioPen(device: AudioPenDevice?) {
    scope.launch {
      if (device == null) {
        _state.value = DeviceListViewState.DeviceBookUpdate(emptyList())
      } else {
        currentlyLoadedDeviceBookInfos = deviceManager
          .first { it.type == device.type }
          .booksFromDevice(device)
        _state.value = DeviceListViewState.DeviceBookUpdate(currentlyLoadedDeviceBookInfos)
      }
    }
  }

  fun filterDeviceBooks(filter: String?) {
    _state.value = DeviceListViewState.DeviceBookUpdate(filter(filter, currentlyLoadedDeviceBookInfos))
  }

  private fun filter(filter: String?, books: List<BookDisplay>): List<BookDisplay> {
    return if (filter == null) {
      books
    } else {
      books.filter { it.title.contains(filter) || it.id.contains(filter) }
    }
  }
}
