package com.andretietz.tingeltangel.manager

import com.andretietz.tingeltangel.pencontract.BookSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch

class ManagerViewModel(
    override val scope: CoroutineScope,
    private val sources: List<BookSource>
) : Interactor {


    private val channel = Channel<ViewState>(Channel.CONFLATED).apply {
        scope.launch {
            val books = sources.map { it.availableBooks() }.flatten()
            send(ViewState.Init(books))
        }
    }
    override val state: ReceiveChannel<ViewState> = channel
}
