package com.andretietz.audiopen.local

import com.andretietz.audiopen.AudioPenDetector
import com.andretietz.audiopen.AudioPenDevice
import com.andretietz.audiopen.Type
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File

class LocalBookStorageDetector(
  private val localBookStore: File
) : AudioPenDetector {

  init {
    if (!localBookStore.exists()) {
      if (!localBookStore.mkdirs()) {
        throw IllegalStateException("Cannot create folder: ${localBookStore.absolutePath}")
      }
    }
  }

  override fun detect(): Flow<AudioPenDetector.DetectorEvent> = flow {
    emit(
      AudioPenDetector.DetectorEvent.Connected(
        AudioPenDevice(
          "00000000-0000-0000-0000-000000000000",
          Type("Local Book Storage", "local"),
          localBookStore
        )
      )
    )
  }
}
