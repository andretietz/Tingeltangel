package com.andretietz.audiopen

import com.andretietz.audiopen.core.script.Disassembler
import java.io.ByteArrayInputStream

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        Disassembler().disassemble(ByteArrayInputStream(arrayOf(0x00, 0x00, 0x10, 0x00, 0x00).map { it.toByte() }.toByteArray()))
    }
}
