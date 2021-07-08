package com.andretietz.audiopen.view.sources

import com.andretietz.audiopen.BookDisplay
import com.andretietz.audiopen.Type

sealed class RemoteSourceViewState(
  open val hasDeviceConnected: Boolean,
  open val bookTypes: List<Type>,
  open val selectedType: Type
) {
  data class Loading(
    override val hasDeviceConnected: Boolean,
    override val bookTypes: List<Type>,
    override val selectedType: Type,
  ) : RemoteSourceViewState(hasDeviceConnected, bookTypes, selectedType)

  data class BookListUpdate(
    override val hasDeviceConnected: Boolean,
    override val bookTypes: List<Type>,
    override val selectedType: Type,
    val bookInfos: List<BookDisplay>
  ) : RemoteSourceViewState(hasDeviceConnected, bookTypes, selectedType)
}
