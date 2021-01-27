package com.andretietz.tingeltangel.manager

import com.andretietz.tingeltangel.pencontract.BookInfo

sealed class ViewState {
    data class Init(
        val books: List<BookInfo>
    ) : ViewState()
}
