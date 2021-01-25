package com.andretietz.audiopen.core

import java.io.InputStream
import kotlin.experimental.and

class Decoder {
    /**
     * decodes a file into a script.
     */
    fun decode(input: InputStream): Script {
//        val buffer = ByteArray(16) // TODO: double check max size
        input.use { stream ->
            val length = stream.available()
            validateStream(stream, length)
            collectJumpTargets(stream, length)
            println(stream.available())
        }

//        var buf = bytes.slice(0..3)
//        if (buf.all { it.toInt() == 0 }) {
//            // TODO: meaningful error!
//            throw IllegalStateException()
//        }
//        while (bytes.isNotEmpty()) {
//
//        }
//    }

        return Script()
    }

    internal fun validateStream(input: InputStream, length: Int) {
        if (input.readNBytes(4).all { it.toInt() == 0 }) {
            // TODO: meaningful error!
            throw IllegalStateException()
        }
        input.skip(length - 4 - 1L)
        if (input.read() != 0) {
            throw IllegalStateException()
        }
        input.reset()
    }

    private fun collectJumpTargets(input: InputStream, length: Int) {
        val opcode = input.read() and 0xFF shl 8 or input.read() and 0xFF
    }
}
