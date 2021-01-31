package com.andretietz.audiopen

import java.io.File
import java.net.URL

sealed class Thumbnail {
  data class Remote(val url: URL) : Thumbnail()
  data class Local(val file: File) : Thumbnail()
}
