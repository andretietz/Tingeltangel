package com.andretietz.audiopen.view

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ReceiveChannel

interface BaseInteractor<T> {
  /**
   * Coroutine Scope.
   */
  val scope: CoroutineScope

  /**
   * channel for listenings to the viewstate.
   */
  val state: ReceiveChannel<T>
}
