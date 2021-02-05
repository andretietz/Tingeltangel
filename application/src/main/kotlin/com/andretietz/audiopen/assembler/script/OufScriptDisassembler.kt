package com.andretietz.audiopen.assembler.script

import java.io.ByteArrayInputStream

class OufScriptDisassembler {

}

@SuppressWarnings("Detekt.MagicNumber", "Detekt.UnnecessaryParentheses")
internal fun ByteArrayInputStream.readPair() = (read() and 0xFF shl 8) or (read() and 0xFF)
