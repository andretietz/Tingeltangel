package com.andretietz.tingeltangel.ui

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
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.andretietz.audiopen.AudioPenDevice
import com.andretietz.audiopen.BookDisplay
import com.andretietz.audiopen.view.devices.DeviceListViewModel
import com.andretietz.audiopen.view.devices.DeviceListViewState

@Composable
fun DeviceTargetView(
  deviceListViewModel: DeviceListViewModel
) {
  val state = deviceListViewModel.state.collectAsState().value

  Column(Modifier.fillMaxSize()) {
    when (state) {
      is DeviceListViewState.NoDeviceConnected -> {
        Box(Modifier.fillMaxSize().align(Alignment.CenterHorizontally)) {
          Row(Modifier.align(Alignment.Center)) {
//            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterVertically))
            Text("Waiting for device...", modifier = Modifier.align(Alignment.CenterVertically))
          }
        }
      }
      is DeviceListViewState.DeviceListUpdate -> {
        Dropdown(state.devices, state.selectedDevice, itemView = { Text(it.type.name) }) {
          deviceListViewModel.selectAudioPen(it)
        }
        Box(Modifier.fillMaxSize()) {
          CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
      }
      is DeviceListViewState.DeviceBookUpdate -> {
        var filterState by remember { mutableStateOf("") }
        Dropdown(state.devices, state.selectedDevice, itemView = { Text(it.type.name) }) {
          deviceListViewModel.selectAudioPen(it)
        }
        OutlinedTextField(
          value = filterState,
          onValueChange = {
            filterState = it
            deviceListViewModel.filterDeviceBooks(it)
          },
          label = { Text("Filter") },
          modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
        )
        LazyColumn(
          contentPadding = PaddingValues(4.dp),
//          state = listState
        ) {
          items(state.books, key = { item -> "${state.selectedDevice.type}+${item.id}" }) { item ->
            BookDisplayItemView(item)
          }
        }
      }
    }
  }
}

@Composable
private fun BookDisplayItemView(item: BookDisplay) {
  Card(modifier = Modifier.fillMaxWidth().padding(4.dp)) {
    Row(modifier = Modifier.fillMaxWidth().height(100.dp)) {
      Text("${item.title} (${item.id})")
    }
  }
}
