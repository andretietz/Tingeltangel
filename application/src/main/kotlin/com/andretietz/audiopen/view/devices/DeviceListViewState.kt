package com.andretietz.audiopen.view.devices

import com.andretietz.audiopen.AudioPenDevice
import com.andretietz.audiopen.BookDisplay

sealed class DeviceListViewState {
  data class DeviceListUpdate(val devices: List<AudioPenDevice>) : DeviceListViewState()
  data class DeviceBookUpdate(val books: List<BookDisplay>) : DeviceListViewState()
}
