package com.dik.settings.widgets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dik.uikit.widgets.AppNormalText
import com.dik.uikit.widgets.AppSmallText

@Composable
fun <T> DropDownListItem(
    header: String,
    selectedItem: T,
    items: List<T>,
    title: (T) -> String,
    modifier: Modifier = Modifier,
    onClickItem: (T) -> Unit,
) {
    val isExpanded = remember { mutableStateOf(false) }

    Column(modifier = modifier.clickable { isExpanded.value = !isExpanded.value }.padding(8.dp)) {
        SelectedItem(header = header, hint = title(selectedItem))
        DropdownMenu(
            expanded = isExpanded.value,
            onDismissRequest = { isExpanded.value = false },
        ) {
            items.forEach { item ->
                Text(
                    text = title(item),
                    modifier = Modifier.clickable {
                        onClickItem(item)
                        isExpanded.value = false
                    }.fillMaxWidth().padding(8.dp)
                )
            }
        }
    }
}

@Composable
fun SelectedItem(header: String, hint: String, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.fillMaxSize()) {
            AppNormalText(header)
            if (!hint.isNullOrEmpty()) AppSmallText(hint)
        }
    }
}