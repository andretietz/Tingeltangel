package com.andretietz.audiopen

import java.io.File

data class AudioPenDevice(
  val uuid: String,
  val type: Type,
  val rootDirectory: File
)
