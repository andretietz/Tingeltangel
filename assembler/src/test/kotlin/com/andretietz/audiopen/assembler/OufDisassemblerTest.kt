package com.andretietz.audiopen.assembler

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.File

class OufDisassemblerTest {

  private val cacheDir = File("src/test/resources/target").also { it.mkdirs() }
  private val disassembler = OufDisassembler(cacheDir)


  @Test
  fun `Importing an ouf file works`() {
    val file = File("src/test/resources/testinput", "08091_en.ouf")

    val book = disassembler.disassemble(file)

    assertThat(book.size).isEqualTo(8)
  }

  @Test
  fun `Collecting labels works`() {
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
      val result = disassembler.collectLabels(it)
      assertThat(result.size).isEqualTo(5)
      val valueList = result.keys.toList()
      assertThat(valueList[0]).isEqualTo(0x18)
      assertThat(valueList[1]).isEqualTo(0x28)
      assertThat(valueList[2]).isEqualTo(0x20)
      assertThat(valueList[3]).isEqualTo(0x12a)
      assertThat(valueList[4]).isEqualTo(0x44)
    }
  }

  @Test
  fun `Disassembling works`() {
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
    val result = disassembler.disassembleScript(ByteArrayInputStream(byteArray))
    assertThat(result).isEqualTo(outcome)
  }

  @Test(expected = IllegalStateException::class)
  fun `Missing Endbyte will lead to IllegalStateException`() {
    val byteArray = intArrayOf(
      0x02, 0x01, 0x00, 0x01, 0x00, 0x1E,     // set v1, 30
      0x16, 0x01, 0x3A, 0x99,                 // playoid 15001
      0x17, 0x02, 0x00, 0x01,                 // pause v1
      0x16, 0x01, 0x3A, 0x9A,                 // playoid 15002
      0x00, 0x00                              // end
      // EOF missing!

    ).map { it.toByte() }.toByteArray()

    disassembler.disassembleScript(ByteArrayInputStream(byteArray))
  }

  @Test
  fun `Labels are being created`() {
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
    val result = disassembler.disassembleScript(ByteArrayInputStream(byteArray))

    assertThat(result).isEqualTo(outcome)
  }

  @Test(expected = IllegalStateException::class)
  fun `Unknown OpCode will lead to IllegalStateException`() {
    val byteArray = intArrayOf(
      0x1F, 0x1F, 0x10,
      0, 0
    ).map { it.toByte() }.toByteArray()

    disassembler.disassembleScript(ByteArrayInputStream(byteArray))
  }

}
