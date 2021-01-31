package com.andretietz.audiopen.view.sources

import com.andretietz.audiopen.BookInfo
import com.andretietz.audiopen.Type

sealed class RemoteSourceViewState {
  data class Init(
    val bookTypes: List<Type>
  ) : RemoteSourceViewState()

  data class BookListUpdate(
    val bookInfos: List<BookInfo>
  ) : RemoteSourceViewState()
}
