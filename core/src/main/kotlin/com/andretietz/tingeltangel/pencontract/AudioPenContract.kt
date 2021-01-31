package com.andretietz.tingeltangel.pencontract

interface AudioPenContract {

  /**
   * Type of the book source.
   */
  val type: Type

  fun remoteBookSource(): RemoteBookSource

  fun deviceBookSource(): AudioPenDeviceManager

  data class Type(
    val name: String,
    val type: String
  ) {
    override fun toString() = name
  }
}
