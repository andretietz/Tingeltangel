package com.andretietz.audiopen.view.sources

import com.andretietz.audiopen.BookInfo
import com.andretietz.audiopen.Type
import com.andretietz.audiopen.remote.RemoteBookSource
import com.andretietz.audiopen.view.BaseViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class RemoteSourceViewModel(
  override val scope: CoroutineScope,
  private val remoteSource: List<RemoteBookSource>,
) : BaseViewModel<RemoteSourceViewState>(scope), RemoteSourceInteractor {


  private lateinit var currentlyLoadedLocalBookInfos: List<BookInfo>

  init {
    channel.apply {
      scope.launch {
        currentlyLoadedLocalBookInfos = remoteSource.first().availableBooks()
        send(RemoteSourceViewState.Init(remoteSource.map { it.type }))
      }
    }
  }

  override fun selectBookSource(sourceType: Type?) {
    scope.launch {
      if (sourceType == null) {
        channel.send(RemoteSourceViewState.BookListUpdate(emptyList()))
      } else {
        currentlyLoadedLocalBookInfos = remoteSource.first { it.type == sourceType }.availableBooks()
        channel.send(RemoteSourceViewState.BookListUpdate(currentlyLoadedLocalBookInfos))
      }
    }
  }

  override fun filterRemoteBooks(filter: String?) {
    scope.launch {
      channel.send(RemoteSourceViewState.BookListUpdate(filter(filter, currentlyLoadedLocalBookInfos)))
    }
  }

  private fun filter(filter: String?, books: List<BookInfo>): List<BookInfo> {
    return if (filter == null) {
      books
    } else {
      books.filter { it.title.contains(filter) || it.id.contains(filter) }
    }
  }
}
