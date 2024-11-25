package com.dik.torrentlist.screens.main.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.dik.torrserverapi.model.Torrent
import com.dik.uikit.utils.currentWindowSize
import com.dik.uikit.widgets.AppAsyncImage
import com.dik.uikit.widgets.AppNormalBoldText
import org.jetbrains.compose.resources.stringResource
import torrservermedia.features.torrentlist.impl.generated.resources.Res
import torrservermedia.features.torrentlist.impl.generated.resources.main_torrent_list_is_empty

@Composable
internal actual fun TorrentListUi(
    component: TorrentListComponent,
    modifier: Modifier
) {
    val uiState = component.uiState.collectAsState()
    val windowSize = currentWindowSize()

    when {
        uiState.value.torrents.isEmpty() -> EmptyListStub(
            modifier = modifier
        )

        else -> Torrents(
            torrents = uiState.value.torrents,
            modifier = modifier,
            onClickItem = { component.onClickItem(it, windowSize) }
        )
    }
}

@Composable
private fun Torrents(
    torrents: List<Torrent>,
    modifier: Modifier = Modifier,
    onClickItem: (Torrent) -> Unit
) {
    LazyVerticalGrid(modifier = modifier, columns = GridCells.Adaptive(100.dp)) {
        items(torrents) { torrent ->
            TorrentItem(torrent = torrent, onClickItem = onClickItem)
        }
    }
}

@Composable
private fun TorrentItem(
    torrent: Torrent,
    modifier: Modifier = Modifier,
    onClickItem: (Torrent) -> Unit = {}
) {
    Card(modifier = modifier
        .padding(2.dp)
        .clickable { onClickItem(torrent) }
        .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .aspectRatio(0.65f)
                .sizeIn(maxWidth = 200.dp, maxHeight = 300.dp)
        ) {
            AppAsyncImage(
                modifier = Modifier.fillMaxSize(),
                url = torrent.poster,
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
private fun EmptyListStub(modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        AppNormalBoldText(
            text = stringResource(Res.string.main_torrent_list_is_empty),
            modifier = Modifier.align(Alignment.Center)
        )
    }
}