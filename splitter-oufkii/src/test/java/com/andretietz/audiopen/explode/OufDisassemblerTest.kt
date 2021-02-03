package com.andretietz.audiopen.explode

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.io.File

class OufDisassemblerTest {


  @Test
  fun `Exploding an ouf file works`() {
    val target = File("src/test/resources/target").also { it.mkdirs() }
    val exploder = OufDisassembler(target)
    val file = File("src/test/resources/dist", "08091_en.ouf")

    val book = exploder.disassemble(file)

    assertThat(book.id).isEqualTo(8091)
    assertThat(book.data.size).isEqualTo(8)
  }


}
