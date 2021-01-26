package com.andretietz.tingeltangel.manager

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ReceiveChannel

interface Interactor {
    /**
     * Coroutine Scope.
     */
    val scope: CoroutineScope

    /**
     * channel for listenings to the viewstate.
     */
    val state: ReceiveChannel<ViewState>
}
