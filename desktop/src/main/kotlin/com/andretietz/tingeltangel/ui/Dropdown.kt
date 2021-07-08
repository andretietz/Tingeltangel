package com.andretietz.tingeltangel.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun <T> Dropdown(
  items: List<T>,
  selected: T = items.first(),
  itemView: @Composable (item: T) -> Unit,
  onSelected: (item: T) -> Unit,
) {
  var expanded by remember { mutableStateOf(false) }
  var selectedState by remember { mutableStateOf(selected) }

  Box(modifier = Modifier.fillMaxWidth()) {
    DropdownButton({ itemView(selectedState) }) { expanded = true }
    DropdownMenu(
      expanded = expanded,
      onDismissRequest = { expanded = false }
    ) {
      items.forEach { item ->
        DropdownMenuItem(onClick = {
          selectedState = item
          expanded = false
          onSelected(item)
        }) {
          Box(Modifier
            .fillMaxWidth()
            .align(Alignment.CenterVertically)
          ) { itemView(item) }
        }
      }
    }
  }
}

@Composable
private fun DropdownButton(draw: @Composable () -> Unit, onClick: () -> Unit) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .padding(8.dp)
  ) {
    Box(Modifier.weight(1f).align(Alignment.CenterVertically)) { draw() }
    Image(imageVector = Icons.Filled.ArrowDropDown, "DropdownArrow", modifier = Modifier.align(Alignment.CenterVertically))
  }
}
