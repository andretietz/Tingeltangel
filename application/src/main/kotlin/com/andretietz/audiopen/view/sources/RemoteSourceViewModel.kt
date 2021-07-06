package com.andretietz.audiopen.view.sources

import com.andretietz.audiopen.BookDisplay
import com.andretietz.audiopen.Type
import com.andretietz.audiopen.remote.BookSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RemoteSourceViewModel(
  private val scope: CoroutineScope,
  private val source: List<BookSource>
) {

  private val _state = MutableStateFlow<RemoteSourceViewState>(RemoteSourceViewState.Init(source.map { it.type }))
  val state: StateFlow<RemoteSourceViewState> = _state

  private var currentlyLoadedLocalBookInfos: List<BookDisplay> = emptyList()

  fun selectBookSource(sourceType: Type?) {
    scope.launch {
      if (sourceType == null) {
        _state.value = RemoteSourceViewState.BookListUpdate(emptyList())
      } else {
        withContext(Dispatchers.IO) {
          currentlyLoadedLocalBookInfos = source.first { it.type == sourceType }.availableBooks()
        }
        _state.value = RemoteSourceViewState.BookListUpdate(currentlyLoadedLocalBookInfos)
      }
    }
  }

  fun filterRemoteBooks(filter: String?) {
    _state.value = RemoteSourceViewState.BookListUpdate(filter(filter, currentlyLoadedLocalBookInfos))
  }

  private fun filter(filter: String?, books: List<BookDisplay>): List<BookDisplay> {
    return if (filter == null) {
      books
    } else {
      books.filter { it.title.contains(filter) || it.id.contains(filter) }
    }
  }
}
