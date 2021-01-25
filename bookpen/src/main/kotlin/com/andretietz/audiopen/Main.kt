package com.andretietz.audiopen

import com.andretietz.audiopen.core.Decoder
import java.io.ByteArrayInputStream

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        Decoder().decode(ByteArrayInputStream(arrayOf(0x00, 0x00, 0x10, 0x00, 0x00).map { it.toByte() }.toByteArray()))
    }
}
