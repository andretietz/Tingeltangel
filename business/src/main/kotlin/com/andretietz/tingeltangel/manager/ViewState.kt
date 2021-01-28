package com.andretietz.tingeltangel.manager

import com.andretietz.tingeltangel.pencontract.BookInfo
import com.andretietz.tingeltangel.pencontract.PenType

sealed class ViewState {

  data class Init(
    val books: List<BookInfo>,
    val bookTypes: List<PenType>
  ) : ViewState()

  data class LocalBookListUpdate(
    val books: List<BookInfo>
  ) : ViewState()
}


