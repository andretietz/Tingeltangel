package com.andretietz.tingeltangel.manager

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ReceiveChannel
import java.io.File
import java.net.URL

interface Interactor {
  /**
   * Coroutine Scope.
   */
  val scope: CoroutineScope

  /**
   * channel for listenings to the viewstate.
   */
  val state: ReceiveChannel<ViewState>

  fun loadImage(url: URL, callback: (file: File) -> Unit)

  fun filterLocalBooks(filter: String?)
  fun filterDeviceBooks(filter: String?)
}
