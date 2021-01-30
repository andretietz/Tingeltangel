package com.andretietz.tingeltangel.pencontract

import java.io.File

interface AudioPenContract {

  /**
   * Type of the book source.
   */
  val type: Type

  fun source(): BookSource

  fun verifyDevice(rootFolder: File): Boolean

  suspend fun booksFromDevice(device: AudioPenDevice): List<Book>

  data class Type(
    val name: String,
    val type: String
  ) {
    override fun toString() = name
  }
}
