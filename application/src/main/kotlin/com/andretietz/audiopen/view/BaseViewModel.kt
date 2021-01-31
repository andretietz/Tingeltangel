package com.andretietz.audiopen.view

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel

abstract class BaseViewModel<T>(protected open val scope: CoroutineScope) {
  protected val channel: Channel<T> = Channel(Channel.CONFLATED)
  val state: ReceiveChannel<T> = channel
}
