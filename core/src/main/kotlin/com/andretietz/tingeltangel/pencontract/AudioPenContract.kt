package com.andretietz.tingeltangel.pencontract

import java.io.File

interface AudioPenContract {

  /**
   * Type of the book source.
   */
  val type: PenType

  fun source(): BookSource

  suspend fun verifyDevice(rootFolder: File): AudioPenDevice?
}

data class PenType(
  val name: String,
  val type: String
)
