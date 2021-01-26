package com.andretietz.audiopen.core.script

import com.andretietz.tingeltangel.core.script.Disassembler
import junit.framework.Assert.assertEquals
import org.junit.Test
import java.io.ByteArrayInputStream

internal class DisassemblerTest {

    @Test
    fun `Collecting labels works`() {
        val decompiler = Disassembler()
        val byteArray = intArrayOf(
            0x03, 0x01, 0x00, 0x50, 0x00, 0x0e,     // cmp v80, 14
            0x0a, 0x00, 0x00, 0x18,                 // jne 0x0018
            0x03, 0x01, 0x00, 0x46, 0x00, 0x0e,     // cmp v70, 14
            0x09, 0x00, 0x00, 0x28,                 // je 0x0028
            0x08, 0x00, 0x00, 0x20,                 // jmp 0x0020
            0x01, 0x00,                             // clearver
            0x02, 0x01, 0x00, 0x50, 0x00, 0x0e,     // set v80, 14
            0x16, 0x01, 0xb0, 0xe2,                 // playoid 45282
            0x08, 0x00, 0x01, 0x2a,                 // jmp 0x012a
            0x02, 0x01, 0x00, 0x16, 0x00, 0x01,     // set v22, 1
            0x03, 0x02, 0x00, 0x16, 0x00, 0x02,     // cmp v22, v2
            0x09, 0x00, 0x00, 0x44,                 // je 0x0044
            0x16, 0x01, 0x7d, 0x00,                 // playoid 32000
            0x16, 0x01, 0xb1, 0xbe,                 // playoid 45502
            0x00, 0x00, 0x00
        ).map { it.toByte() }.toByteArray()

        ByteArrayInputStream(byteArray).use {
            val result = decompiler.collectLabels(it)
            assertEquals(5, result.size)
            val valueList = result.keys.toList()
            assertEquals(0x18, valueList[0])
            assertEquals(0x28, valueList[1])
            assertEquals(0x20, valueList[2])
            assertEquals(0x12a, valueList[3])
            assertEquals(0x44, valueList[4])
        }
    }

    @Test
    fun `Disassembling works`() {
        val decompiler = Disassembler()
        val outcome = """set v1, 30
playoid 15001
pause v1
playoid 15002
end
"""

        val byteArray = intArrayOf(
            0x02, 0x01, 0x00, 0x01, 0x00, 0x1E,     // set v1, 30
            0x16, 0x01, 0x3A, 0x99,                 // playoid 15001
            0x17, 0x02, 0x00, 0x01,                 // pause v1
            0x16, 0x01, 0x3A, 0x9A,                 // playoid 15002
            0x00, 0x00,                             // end
            0x00                                    // EOF

        ).map { it.toByte() }.toByteArray()
        val result = decompiler.disassemble(ByteArrayInputStream(byteArray))

        assertEquals(outcome, result)
    }

    @Test(expected = IllegalStateException::class)
    fun `Missing Endbyte will lead to IllegalStateException`() {
        val decompiler = Disassembler()
        val byteArray = intArrayOf(
            0x02, 0x01, 0x00, 0x01, 0x00, 0x1E,     // set v1, 30
            0x16, 0x01, 0x3A, 0x99,                 // playoid 15001
            0x17, 0x02, 0x00, 0x01,                 // pause v1
            0x16, 0x01, 0x3A, 0x9A,                 // playoid 15002
            0x00, 0x00                              // end
            // EOF missing!

        ).map { it.toByte() }.toByteArray()

        decompiler.disassemble(ByteArrayInputStream(byteArray))
    }

    @Test
    fun `Labels are being created`() {
        val decompiler = Disassembler()
        val outcome = """set v1, 30
playoid 15001
jmp l1
playoid 15001

:l1
playoid 15002
end
"""
        val byteArray = intArrayOf(
            0x02, 0x01, 0x00, 0x01, 0x00, 0x1E,     // set v1, 30
            0x16, 0x01, 0x3A, 0x99,                 // playoid 15001
            0x08, 0x00, 0x00, 0x12,                 // jmp to "playoid 15002"
            0x16, 0x01, 0x3A, 0x99,                 // playoid 15001
            0x16, 0x01, 0x3A, 0x9A,                 // playoid 15002
            0x00, 0x00,                             // end
            0x00                                    // EOF

        ).map { it.toByte() }.toByteArray()
        val result = decompiler.disassemble(ByteArrayInputStream(byteArray))

        assertEquals(outcome, result)
    }

    @Test(expected = IllegalStateException::class)
    fun `Unknown OpCode will lead to IllegalStateException`() {
        val decompiler = Disassembler()
        val byteArray = intArrayOf(
            0x1F, 0x1F, 0x10,
            0, 0
        ).map { it.toByte() }.toByteArray()

        decompiler.disassemble(ByteArrayInputStream(byteArray))
    }
}
