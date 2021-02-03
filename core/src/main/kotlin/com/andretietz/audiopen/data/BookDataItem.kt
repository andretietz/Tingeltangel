package com.andretietz.audiopen.data

import java.io.File

sealed class BookDataItem(
  open val code: Int,
  open val size: Int,
  open val name: String
) {
  data class MP3(
    override val code: Int,
    override val size: Int,
    override val name: String,
    val file: File
  ) : BookDataItem(code, size, name)

  data class Script(
    override val code: Int,
    override val size: Int,
    override val name: String,
    val script: String
  ) : BookDataItem(code, size, name)

  companion object {
    const val TYPE_AUDIO  = 0x0001
    const val TYPE_SCRIPT = 0x0002
  }
}
