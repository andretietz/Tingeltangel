package com.andretietz.tingeltangel.devicedetector

import com.andretietz.tingeltangel.pencontract.AudioPenContract
import com.andretietz.tingeltangel.pencontract.AudioPenDetector
import com.andretietz.tingeltangel.pencontract.AudioPenDevice
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
  private val contracts: List<AudioPenContract>,
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
            offer(AudioPenDetector.DetectorEvent.Disconnected(device))
          } else if (event.eventType == DeviceEventType.CONNECTED) {
            if (contract.verifyDevice(event.storageDevice.rootDirectory)) {
              offer(AudioPenDetector.DetectorEvent.Connected(device))
            }
          }
//          contract.verifyDevice(event.storageDevice.rootDirectory)?.let {
//            when (event.eventType) {
//              DeviceEventType.CONNECTED -> offer(AudioPenDetector.DetectorEvent.Connected(it))
//              DeviceEventType.REMOVED -> offer(AudioPenDetector.DetectorEvent.Disconnected(it))
//              else -> {
//              }
//            }
//          }
        }
      }
    }
    awaitClose { cancel() }
  }
}
