package com.andretietz.tingeltangel.manager

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel

class ManagerViewModel(override val scope: CoroutineScope) : Interactor {
    private val channel = Channel<ViewState>(Channel.CONFLATED)
    override val state: ReceiveChannel<ViewState> = channel
}
