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
  private val source: List<BookSource>,
  private val copyBook: (book: BookDisplay) -> Unit
) {

  private val _state = MutableStateFlow<RemoteSourceViewState>(
    RemoteSourceViewState.Loading(
      false,
      source.map { it.type },
      source.map { it.type }.first()
    ))
  val state: StateFlow<RemoteSourceViewState> = _state

  private var currentlyLoadedLocalBookInfos: List<BookDisplay> = emptyList()

  init {
    selectBookSource(_state.value.selectedType)
  }

  fun selectBookSource(sourceType: Type) {
    scope.launch {
      _state.value = RemoteSourceViewState.Loading(
        _state.value.hasDeviceConnected,
        source.map { it.type },
        sourceType
      )
      withContext(Dispatchers.IO) {
        currentlyLoadedLocalBookInfos = source.first { it.type == sourceType }.availableBooks()
      }
      _state.value = RemoteSourceViewState.BookListUpdate(
        _state.value.hasDeviceConnected,
        _state.value.bookTypes,
        sourceType,
        currentlyLoadedLocalBookInfos)
    }
  }

  fun filterRemoteBooks(filter: String?) {
    _state.value = RemoteSourceViewState.BookListUpdate(
      _state.value.hasDeviceConnected,
      _state.value.bookTypes,
      _state.value.selectedType,
      filter(filter, currentlyLoadedLocalBookInfos))
  }

  private fun filter(filter: String?, books: List<BookDisplay>): List<BookDisplay> {
    return if (filter == null) {
      books
    } else {
      books.filter { it.title.lowercase().contains(filter.lowercase()) || it.id.contains(filter) }
    }
  }

  fun transferBook(bookDisplay: BookDisplay) = copyBook(bookDisplay)

  fun deviceConnected(connected: Boolean) {
    when (val state = _state.value) {
      is RemoteSourceViewState.Loading -> {
        _state.value = RemoteSourceViewState.Loading(connected, state.bookTypes, state.selectedType)
      }
      is RemoteSourceViewState.BookListUpdate -> {
        _state.value = RemoteSourceViewState.BookListUpdate(connected, state.bookTypes, state.selectedType, state.bookInfos)
      }
    }
  }
}
