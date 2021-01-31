package com.andretietz.audiopen.view.devices

import com.andretietz.audiopen.AudioPenDevice
import com.andretietz.audiopen.view.BaseInteractor

interface DeviceListInteractor : BaseInteractor<DeviceListViewState> {
  fun selectAudioPen(device: AudioPenDevice?)
  fun filterDeviceBooks(filter: String?)
}
