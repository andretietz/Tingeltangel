package com.andretietz.tingeltangel.manager

import com.andretietz.audiopen.AudioPenDevice
import com.andretietz.audiopen.BookInfo
import com.andretietz.audiopen.Type

sealed class ViewState {

  data class Init(
    val bookTypes: List<Type>
  ) : ViewState()

  data class LocalBookListUpdate(
    val bookInfos: List<BookInfo>
  ) : ViewState()

  data class DeviceListUpdate(val devices: List<AudioPenDevice>) : ViewState()

  data class DeviceBookUpdate(val books: List<BookInfo>) : ViewState()
}
