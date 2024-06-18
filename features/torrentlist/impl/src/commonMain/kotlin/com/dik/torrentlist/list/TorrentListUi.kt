package com.dik.torrentlist.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
internal fun TorrentListUi(component: TorrentListComponent, modifier: Modifier = Modifier) {
    Column {
        repeat(10) { i ->
            Row(modifier = Modifier.clickable { component.onClickItem(Torrent("URL $i")) }) {
                Text("Row text $i")
            }
        }
    }
}