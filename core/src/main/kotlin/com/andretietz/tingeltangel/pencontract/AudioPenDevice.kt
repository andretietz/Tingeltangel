package com.andretietz.tingeltangel.pencontract

import java.io.File

data class AudioPenDevice(
  val uuid: String,
  val type: String,
  val rootDirectory: File
)
