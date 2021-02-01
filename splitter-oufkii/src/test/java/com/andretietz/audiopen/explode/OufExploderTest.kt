package com.andretietz.audiopen.explode

import okhttp3.internal.toHexString
import org.junit.Assert.*
import org.junit.Test
import java.io.File

class OufExploderTest {
  @Test
  fun asd() {
    val target = File("src/test/resources/target").also { it.mkdirs() }
    val exploder = OufExploder(target)
    val file = File("src/test/resources/dist", "05003_en.ouf")



    exploder.explode(file)
  }
}
