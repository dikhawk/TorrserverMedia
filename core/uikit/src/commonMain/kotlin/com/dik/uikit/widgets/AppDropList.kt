package com.dik.uikit.widgets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun <T> AppDropList(
    selectedItem: T,
    items: List<T>,
    title: (T) -> String,
    modifier: Modifier = Modifier,
    onClickItem: (T) -> Unit,
) {
    val isExpanded = remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        SelectedItem(
            text = title(selectedItem),
            modifier.clickable { isExpanded.value = !isExpanded.value })
        DropdownMenu(
            expanded = isExpanded.value,
            onDismissRequest = { isExpanded.value = false }
        ) {
            items.forEach { item ->
                Text(
                    text = title(item),
                    modifier = Modifier.clickable {
                        onClickItem(item)
                        isExpanded.value = false
                    }.padding(8.dp)
                )
            }
        }
    }
}

@Composable
private fun SelectedItem(text: String, modifier: Modifier = Modifier) {
    Text(text = text, modifier = modifier.fillMaxWidth().padding(8.dp))
}