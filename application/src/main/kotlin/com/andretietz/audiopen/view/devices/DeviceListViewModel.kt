package com.andretietz.audiopen.view.devices

import com.andretietz.audiopen.AudioPenDetector
import com.andretietz.audiopen.AudioPenDevice
import com.andretietz.audiopen.BookDisplay
import com.andretietz.audiopen.device.DeviceManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DeviceListViewModel(
  private val scope: CoroutineScope,
  private val deviceManager: List<DeviceManager>,
  private val deviceDetector: AudioPenDetector,
  private val onDeviceListChange: (devices: List<AudioPenDevice>, selected: AudioPenDevice?) -> Unit
) {

  private val _state = MutableStateFlow<DeviceListViewState>(DeviceListViewState.NoDeviceConnected)

  /**
   * channel for listenings to the viewstate.
   */
  val state: StateFlow<DeviceListViewState> = _state

  private lateinit var currentlyLoadedDeviceBookInfos: List<BookDisplay>
  private val connectedDevices: MutableList<AudioPenDevice> = mutableListOf()
  private var selectedDevice: AudioPenDevice? = null

  init {
    scope.launch {
      withContext(Dispatchers.Default) {
        deviceDetector.detect()
          .collect { event ->
            when (event) {
              is AudioPenDetector.DetectorEvent.Connected -> {
                connectedDevices.add(event.device)
                if (selectedDevice != null) {
                  _state.value = DeviceListViewState.DeviceListUpdate(connectedDevices, event.device)
                } else {
                  _state.value = DeviceListViewState.DeviceListUpdate(connectedDevices, connectedDevices.first())
                  selectAudioPen(event.device)
                }
                onDeviceListChange(connectedDevices, event.device)
              }
              is AudioPenDetector.DetectorEvent.Disconnected -> {
                connectedDevices.remove(event.device)
                if (connectedDevices.isNotEmpty()) {
                  _state.value = DeviceListViewState.DeviceListUpdate(connectedDevices, connectedDevices.first())
                } else {
                  _state.value = DeviceListViewState.NoDeviceConnected
                }
                onDeviceListChange(connectedDevices, null)
              }
            }
          }
      }
    }
  }

  fun selectAudioPen(device: AudioPenDevice) {
    scope.launch(Dispatchers.Default) {
      currentlyLoadedDeviceBookInfos = deviceManager
        .firstOrNull { it.type == device.type }
        ?.booksFromDevice(device)
        ?: throw IllegalStateException("Couldn't find a DeviceManager that can handle device type: ${device.type.type}")
      _state.value = DeviceListViewState.DeviceBookUpdate(
        devices = connectedDevices,
        device,
        currentlyLoadedDeviceBookInfos)
    }
  }

  fun filterDeviceBooks(filter: String?) {
    _state.value = DeviceListViewState.DeviceBookUpdate(
      connectedDevices,
      selectedDevice!!,
      filter(filter, currentlyLoadedDeviceBookInfos))
  }

  private fun filter(filter: String?, books: List<BookDisplay>): List<BookDisplay> {
    return if (filter == null) {
      books
    } else {
      books.filter { it.title.contains(filter) || it.id.contains(filter) }
    }
  }
}
