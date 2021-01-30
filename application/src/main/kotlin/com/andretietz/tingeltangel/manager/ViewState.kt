package com.andretietz.tingeltangel.manager

import com.andretietz.tingeltangel.pencontract.AudioPenContract
import com.andretietz.tingeltangel.pencontract.AudioPenDevice
import com.andretietz.tingeltangel.pencontract.BookInfo

sealed class ViewState {

  data class Init(
    val bookInfos: List<BookInfo>,
    val bookTypes: List<AudioPenContract.Type>
  ) : ViewState()

  data class LocalBookListUpdate(
    val bookInfos: List<BookInfo>
  ) : ViewState()

  data class DeviceListUpdate(val devices: List<AudioPenDevice>) : ViewState()

  data class DeviceBookUpdate(val books: List<BookInfo>) : ViewState()
}


