package com.andretietz.tingeltangel.devicedetector

import com.andretietz.audiopen.AudioPenDetector
import com.andretietz.audiopen.AudioPenDevice
import com.andretietz.audiopen.Type
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File

class DummyAudioPenDetector(
  private val dummyDeviceRoot: File
) : AudioPenDetector {

  init {
    if (!dummyDeviceRoot.exists()) {
      if (!dummyDeviceRoot.mkdirs()) {
        throw IllegalStateException("Cannot create folder: ${dummyDeviceRoot.absolutePath}")
      }
    }
  }

  override fun detect(): Flow<AudioPenDetector.DetectorEvent> = flow {
    emit(AudioPenDetector.DetectorEvent.Connected(
      AudioPenDevice("dummy", Type("dummy", "dummy"), dummyDeviceRoot)
    ))
  }
}
