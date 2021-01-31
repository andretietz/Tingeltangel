package com.andretietz.audiopen.view.sources

import com.andretietz.audiopen.Type
import com.andretietz.audiopen.view.BaseInteractor

interface RemoteSourceInteractor : BaseInteractor<RemoteSourceViewState> {
  fun selectBookSource(sourceType: Type?)
  fun filterRemoteBooks(filter: String?)
}
