package com.andretietz.audiopen.explode

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class CodePositionHelperTest {

  @Test(expected = IllegalArgumentException::class)
  fun `codeFromPosition throws an error invalid position`() {
    assertThat(CodePositionHelper.getCodeFromPosition(0xFF, 1))
  }

  @Test(expected = IllegalArgumentException::class)
  fun `codeFromPosition throws an error invalid n`() {
    assertThat(CodePositionHelper.getCodeFromPosition(0x3F, -1))
  }

  @Test
  fun `codeFromPosition works results`() {
    assertThat(CodePositionHelper.getCodeFromPosition(0x0100, 0)).isEqualTo(189696)
    assertThat(CodePositionHelper.getCodeFromPosition(0x0100, 1)).isEqualTo(184064)
    assertThat(CodePositionHelper.getCodeFromPosition(0x0100, 2)).isEqualTo(141568)
    assertThat(CodePositionHelper.getCodeFromPosition(0x1100, 0)).isEqualTo(185600)
    assertThat(CodePositionHelper.getCodeFromPosition(0x1000, 0)).isEqualTo(185344)
    assertThat(CodePositionHelper.getCodeFromPosition(0xF100, 0)).isEqualTo(259328)
  }

  @Test
  fun `positionFromCode works results`() {
    assertThat(CodePositionHelper.getPositionFromCode(189696, 0)).isEqualTo(0x0100)
    assertThat(CodePositionHelper.getPositionFromCode(184064, 1)).isEqualTo(0x0100)
    assertThat(CodePositionHelper.getPositionFromCode(141568, 2)).isEqualTo(0x0100)
    assertThat(CodePositionHelper.getPositionFromCode(185600, 0)).isEqualTo(0x1100)
    assertThat(CodePositionHelper.getPositionFromCode(185344, 0)).isEqualTo(0x1000)
    assertThat(CodePositionHelper.getPositionFromCode(259328, 0)).isEqualTo(0xF100)
  }

  @Test(expected = IllegalArgumentException::class)
  fun `positionFromCode throws an error invalid position`() {
    assertThat(CodePositionHelper.getPositionFromCode(0xFF, 1))
  }

  @Test(expected = IllegalArgumentException::class)
  fun `positionFromCode throws an error invalid n`() {
    assertThat(CodePositionHelper.getPositionFromCode(0x3F, -1))
  }
}
