package com.andretietz.tingeltangel.ui

import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

object TingeltangelTheme {
  val colors = lightColors()
}

@Composable
fun TingeltangelTheme(
  content: @Composable () -> Unit
) {
  MaterialTheme(
    colors = TingeltangelTheme.colors
  ) {
    content()
  }
}
