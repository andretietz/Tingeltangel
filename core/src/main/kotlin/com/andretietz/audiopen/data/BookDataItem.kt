package com.andretietz.audiopen.data

import java.io.File

sealed class BookDataItem(
  open val code: Int,
  open val file: File
) {
  data class MP3(
    override val code: Int,
    override val file: File
  ) : BookDataItem(code, file)

  data class Script(
    override val code: Int,
    override val file: File
  ) : BookDataItem(code, file)

  companion object {
    const val TYPE_AUDIO = 0x0001
    const val TYPE_SCRIPT = 0x0002
  }
}
