package com.andretietz.audiopen.explode

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.io.File

class OufExploderTest {


  @Test
  fun `Exploding an ouf file works`() {
    val target = File("src/test/resources/target").also { it.mkdirs() }
    val exploder = OufExploder(target)
    val file = File("src/test/resources/dist", "08091_en.ouf")

    val book = exploder.explode(file)

    assertThat(book.id).isEqualTo(8091)
    assertThat(book.data.size).isEqualTo(8)
  }
}
