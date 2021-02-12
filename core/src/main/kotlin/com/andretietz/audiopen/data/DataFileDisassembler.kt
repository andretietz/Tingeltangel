package com.andretietz.audiopen.data

import java.io.File

interface DataFileDisassembler {
  fun disassemble(file: File): List<BookItem>
}
