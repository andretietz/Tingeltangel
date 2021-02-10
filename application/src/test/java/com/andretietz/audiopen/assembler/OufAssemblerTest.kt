package com.andretietz.audiopen.assembler

import com.andretietz.audiopen.data.Book
import com.andretietz.audiopen.data.BookItem
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.io.File
import java.util.Date


class OufAssemblerTest {
  @Test
  fun `String#removeCommentsAndTrim removes the whole line`() {
    assertThat("   // This is a whole line as comment".removeCommentsAndTrim()).isNull()
  }
  @Test
  fun `String#removeCommentsAndTrim removes inline comment and lowercases`() {
    assertThat(" someCommand // foobar".removeCommentsAndTrim()).isEqualTo("somecommand")
  }

  @Test
  fun `String#removeCommentsAndTrim keeps strings without comments as they are and trim`() {
    assertThat(" foo bar   ".removeCommentsAndTrim()).isEqualTo("foo bar")
  }

  @Test
  fun `write ouf header`() {
    val book = Book(
      12345,
      setOf(
        BookItem.MP3(15001, File("src/test/resources/testinput", "15001.mp3"))
      )
    )
    val file = File("src/test/resources/assembly", "12345_en.ouf")
    val assembler = OufAssembler(book)

    file.parentFile.mkdirs()
    file.createNewFile()
    file.outputStream().use {
      assembler.writeHeader(it, 1, Date().time.toInt())
    }


  }
}
