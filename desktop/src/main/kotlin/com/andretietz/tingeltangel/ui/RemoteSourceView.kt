package com.andretietz.tingeltangel.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FileCopy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.andretietz.audiopen.BookDisplay
import com.andretietz.audiopen.view.sources.RemoteSourceViewModel
import com.andretietz.audiopen.view.sources.RemoteSourceViewState

@Composable
fun RemoteSourceView(
  remoteSourceViewModel: RemoteSourceViewModel
) {
  val state = remoteSourceViewModel.state.collectAsState().value

  Column(Modifier.fillMaxSize()) {
    Dropdown(state.bookTypes, state.selectedType, itemView = { Text(it.name) }) {
      remoteSourceViewModel.selectBookSource(it)
    }
    when (state) {
      is RemoteSourceViewState.Loading -> {
        Box(Modifier.fillMaxSize()) {
          CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
      }
      is RemoteSourceViewState.BookListUpdate -> {
        var filterState by remember { mutableStateOf("") }
        OutlinedTextField(
          value = filterState,
          onValueChange = {
            filterState = it
            remoteSourceViewModel.filterRemoteBooks(it)
          },
          label = { Text("Filter") },
          modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
        )
//        val listState = rememberLazyListState()
        LazyColumn(
          contentPadding = PaddingValues(4.dp),
//          state = listState
        ) {
          items(state.bookInfos, key = { item -> "${state.selectedType.type}+${item.id}" }) { item ->
            BookDisplayItemView(item, state.hasDeviceConnected) {
              remoteSourceViewModel.transferBook(it)
            }
          }
        }
      }
    }
  }
}

@Composable
private fun BookDisplayItemView(
  item: BookDisplay,
  deviceConnected: Boolean,
  copyBook: (item: BookDisplay) -> Unit
) {
  var menuShown by remember { mutableStateOf(false) }
  Card(modifier = Modifier
    .fillMaxWidth()
    .padding(4.dp)
  ) {
    Row(modifier = Modifier.fillMaxWidth().height(100.dp)) {
      Text("${item.title} (${item.id})", modifier = Modifier
        .weight(1f)
        .align(Alignment.CenterVertically))
      IconButton(
        onClick = { menuShown = true },
        modifier = Modifier.align(Alignment.CenterVertically),
        enabled = deviceConnected
      ) {
        Image(
          imageVector = Icons.Outlined.FileCopy,
          "DropdownArrow",
        )
        DropdownMenu(
          expanded = menuShown,
          onDismissRequest = { menuShown = false }
        ) {
          DropdownMenuItem(
            onClick = {
              menuShown = false
              copyBook(item)
            }
          ) {
            Box(Modifier
              .fillMaxWidth()
              .align(Alignment.CenterVertically)
            ) { Text("Copy to Device") }
          }
        }
      }
    }

  }
}
