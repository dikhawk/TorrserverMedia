package com.dik.torrentlist.screens.details.files

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier

@Composable
internal fun ContentFilesUi(component: ContentFilesComponent, modifier: Modifier = Modifier) {
    val uiState = component.uiState.collectAsState()

    LazyColumn(modifier = modifier) {
        item {
            Text("ContentFilesUi")
        }
        items(uiState.value.files) { file ->
            Text(file.path, modifier = Modifier.clickable { component.onClickItemPlay(file) })
        }
    }
}