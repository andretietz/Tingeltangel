package com.andretietz.audiopen.data

import java.io.File

sealed class BookDataItem(
  open val code: Int
) {
  data class MP3(
    override val code: Int,
    val file: File
  ) : BookDataItem(code)

  data class Script(
    override val code: Int,
    val script: String,
    val isSubRoutine: Boolean
  ) : BookDataItem(code)

  companion object {
    const val TYPE_AUDIO = 0x0001
    const val TYPE_SCRIPT = 0x0002
  }
}
