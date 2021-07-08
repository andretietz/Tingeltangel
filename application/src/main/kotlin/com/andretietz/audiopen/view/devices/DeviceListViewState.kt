package com.andretietz.audiopen.view.devices

import com.andretietz.audiopen.AudioPenDevice
import com.andretietz.audiopen.BookDisplay

sealed class DeviceListViewState {
  object NoDeviceConnected : DeviceListViewState()
  data class DeviceListUpdate(
    val devices: List<AudioPenDevice>,
    val selectedDevice: AudioPenDevice
  ) : DeviceListViewState()

  data class DeviceBookUpdate(
    val devices: List<AudioPenDevice>,
    val selectedDevice: AudioPenDevice,
    val books: List<BookDisplay>
  ) : DeviceListViewState()
}
