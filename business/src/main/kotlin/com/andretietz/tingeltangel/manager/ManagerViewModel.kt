package com.andretietz.tingeltangel.manager

import com.andretietz.tingeltangel.pencontract.AudioPenContract
import com.andretietz.tingeltangel.pencontract.BookInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch
import java.io.File
import java.net.URL

class ManagerViewModel(
  override val scope: CoroutineScope,
  private val imageCache: ImageCache,
  private val sources: List<AudioPenContract>
) : Interactor {

  private lateinit var currentlyLoadedLocalBooks: List<BookInfo>

  private val channel = Channel<ViewState>(Channel.CONFLATED).apply {
    scope.launch {
      currentlyLoadedLocalBooks = sources.first().source().availableBooks()
      send(ViewState.Init(
        currentlyLoadedLocalBooks,
        sources.map { it.type }
      ))
    }
  }
  override val state: ReceiveChannel<ViewState> = channel
  override fun loadImage(url: URL, callback: (file: File) -> Unit) {
    imageCache.image(url, callback)
  }

  override fun filterLocalBooks(filter: String?) {
    if (filter == null) {
      scope.launch {
        channel.send(ViewState.LocalBookListUpdate(currentlyLoadedLocalBooks))
      }
    } else {
      scope.launch {
        channel.send(ViewState.LocalBookListUpdate(currentlyLoadedLocalBooks.filter { it.title.contains(filter) }))
      }
    }
  }

  override fun filterDeviceBooks(filter: String?) {
    // TODO
  }
}
