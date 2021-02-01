package com.andretietz.audiopen.data

import java.io.File

interface DataFileExploder {
  fun explode(file: File): BookData
}
