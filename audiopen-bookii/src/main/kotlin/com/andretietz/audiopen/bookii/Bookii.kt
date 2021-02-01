package com.andretietz.audiopen.bookii

import com.andretietz.audiopen.Type

internal object Bookii {
  private const val STRING_TYPE = "bookii"
  private const val STRING_NAME = "Bookii"
  internal const val API_BASE_URL = "https://www.bookii-medienservice.de/Medienserver-1.0/api/"
  val AUDIOPEN_TYPE = Type(STRING_NAME, STRING_TYPE)
}
