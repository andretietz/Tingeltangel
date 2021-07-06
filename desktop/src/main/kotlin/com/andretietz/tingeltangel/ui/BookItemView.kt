package com.andretietz.tingeltangel.ui

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.andretietz.audiopen.BookDisplay

@Composable
fun BookItemView(book: BookDisplay) {
  Text(book.title)
}


