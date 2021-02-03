package com.andretietz.audiopen.assembler

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test


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
}
