package com.andretietz.audiopen

/**
 * Type of audio-pen.
 */
data class Type(
  /**
   * Human readable name of the audio-pen.
   */
  val name: String,
  /**
   * Internal Type string of that audio-pen.
   */
  val type: String
)
