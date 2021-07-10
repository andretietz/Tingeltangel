package com.andretietz.tingeltangel.devicedetector

import com.andretietz.audiopen.AudioPenDetector
import kotlinx.coroutines.flow.merge

class CompositeAudioPenDetector(
  private val audioPenDetectors: List<AudioPenDetector>
) : AudioPenDetector {
  override fun detect() = audioPenDetectors.map { it.detect() }.merge()
}
