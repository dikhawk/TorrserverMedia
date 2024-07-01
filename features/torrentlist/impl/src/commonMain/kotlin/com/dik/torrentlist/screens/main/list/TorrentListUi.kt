package com.dik.torrentlist.screens.main.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dik.uikit.widgets.ImageAasyncContentScale
import com.dik.uikit.widgets.ImageAsync

@Composable
internal fun TorrentListUi(component: TorrentListComponent, modifier: Modifier = Modifier) {
    val uiState = component.uiState.collectAsState()

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 180.dp)
    ) {
        items(uiState.value.torrents, key = { it.hash }) { torrent ->

            Column(modifier = Modifier.padding(4.dp)) {
                ImageAsync(
                    modifier = Modifier.fillMaxSize(),
                    url = torrent.poster,
                    contentScale = ImageAasyncContentScale.FIT
                )
                Text(torrent.title)
            }
        }
    }
}