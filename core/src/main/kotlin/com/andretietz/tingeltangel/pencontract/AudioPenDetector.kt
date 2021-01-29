package com.andretietz.tingeltangel.pencontract

import kotlinx.coroutines.flow.Flow

interface AudioPenDetector {
  fun detect(): Flow<DetectorEvent>

  sealed class DetectorEvent(
    open val device: AudioPenDevice
  ) {
    data class Connected(override val device: AudioPenDevice) : DetectorEvent(device)
    data class Disconnected(override val device: AudioPenDevice) : DetectorEvent(device)
  }
}
