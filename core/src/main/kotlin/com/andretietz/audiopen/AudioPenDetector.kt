package com.andretietz.audiopen

import com.andretietz.audiopen.AudioPenDetector.DetectorEvent.Connected
import com.andretietz.audiopen.AudioPenDetector.DetectorEvent.Disconnected
import kotlinx.coroutines.flow.Flow

/**
 * The audiopen detector can send events if a device was connected/disconnected.
 * Since this is very different on different operating systems, we need an abstraction for each.
 */
interface AudioPenDetector {
  /**
   * @return a [Flow]-stream containing all connect/disconnect events for new devices.
   */
  fun detect(): Flow<DetectorEvent>

  /**
   * The [DetectorEvent] defines contains a certain device and has 2 states - [Connected] and [Disconnected].
   */
  sealed class DetectorEvent(
    open val device: AudioPenDevice
  ) {
    /**
     * Emitted when a new [AudioPenDevice] connects.
     */
    data class Connected(override val device: AudioPenDevice) : DetectorEvent(device)

    /**
     * Emitted when an [AudioPenDevice] disconnects.
     */
    data class Disconnected(override val device: AudioPenDevice) : DetectorEvent(device)
  }
}
