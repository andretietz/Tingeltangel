package com.andretietz.audiopen.local

import com.andretietz.audiopen.Type

object LocalStore {
  private const val STRING_TYPE = "local"
  private const val STRING_NAME = "Local Storage"
  val AUDIOPEN_TYPE = Type(STRING_NAME, STRING_TYPE)
}
