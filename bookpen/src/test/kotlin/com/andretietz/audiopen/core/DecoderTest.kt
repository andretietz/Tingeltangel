package com.andretietz.audiopen.core

import org.junit.Test
import java.io.ByteArrayInputStream


internal class DecoderTest {


    @Test(expected = IllegalStateException::class)
    fun `Null header leads to IllegalStateException`() {
        val decoder = Decoder()
        decoder.decode(ByteArrayInputStream(arrayOf(0x00, 0x00, 0x00, 0x00).map { it.toByte() }.toByteArray()))
    }

    @Test(expected = IllegalStateException::class)
    fun `Too short header leads to IllegalStateException`() {
        val decoder = Decoder()
        decoder.decode(ByteArrayInputStream(arrayOf(0x00, 0x00, 0x00).map { it.toByte() }.toByteArray()))
    }
    @Test
    fun `Test if validation works`() {
        val decoder = Decoder()
        ByteArrayInputStream(arrayOf(0xDE, 0xAD, 0xC0, 0xDE, 0x00).map { it.toByte() }.toByteArray()).use {
            decoder.validateStream(it, it.available())
        }
    }

}
