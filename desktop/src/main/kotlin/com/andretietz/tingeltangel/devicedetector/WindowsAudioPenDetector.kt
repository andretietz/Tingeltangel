package com.andretietz.tingeltangel.devicedetector

import com.andretietz.audiopen.AudioPenDetector
import com.andretietz.audiopen.AudioPenDevice
import com.andretietz.audiopen.device.DeviceManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import net.samuelcampos.usbdrivedetector.USBDeviceDetectorManager
import net.samuelcampos.usbdrivedetector.events.DeviceEventType

class WindowsAudioPenDetector(
  private val contracts: List<DeviceManager>,
  private val scope: CoroutineScope
) : AudioPenDetector {

  @ExperimentalCoroutinesApi
  override fun detect(): Flow<AudioPenDetector.DetectorEvent> = callbackFlow {
    USBDeviceDetectorManager().addDriveListener { event ->
      contracts.map { contract ->
        scope.launch {
          val device = AudioPenDevice(
            event.storageDevice.uuid,
            contract.type,
            event.storageDevice.rootDirectory
          )
          if (event.eventType == DeviceEventType.REMOVED) {
            trySend(AudioPenDetector.DetectorEvent.Disconnected(device))
          } else if (event.eventType == DeviceEventType.CONNECTED) {
            if (contract.verifyDevice(event.storageDevice.rootDirectory)) {
              trySend(AudioPenDetector.DetectorEvent.Connected(device))
            }
          }
        }
      }
    }
    awaitClose { cancel() }
  }
}
