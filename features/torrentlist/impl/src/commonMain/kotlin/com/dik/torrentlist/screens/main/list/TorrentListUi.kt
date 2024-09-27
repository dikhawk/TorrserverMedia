package com.dik.torrentlist.screens.main.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dik.torrentlist.converters.toReadableSize
import com.dik.torrserverapi.model.Torrent
import com.dik.uikit.widgets.AppAsyncImage
import com.dik.uikit.widgets.AppNormalBoldText
import com.dik.uikit.widgets.AppNormalText
import com.dik.uikit.widgets.AppStubVideo
import org.jetbrains.compose.resources.stringResource
import torrservermedia.features.torrentlist.impl.generated.resources.Res
import torrservermedia.features.torrentlist.impl.generated.resources.main_torrent_list_files_count
import torrservermedia.features.torrentlist.impl.generated.resources.main_torrent_list_is_empty

@Composable
internal fun TorrentListUi(component: TorrentListComponent, modifier: Modifier = Modifier) {
    val uiState = component.uiState.collectAsState()

    Box(modifier = modifier.fillMaxSize(),contentAlignment = Alignment.Center) {
        when {
            uiState.value.torrents.isEmpty() -> EmptyListStub()
            else -> Torrents(uiState.value.torrents, component)
        }
    }
}

@Composable
private fun Torrents(
    torrents: List<Torrent>,
    component: TorrentListComponent,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        modifier = modifier.fillMaxSize(),
        columns = GridCells.Adaptive(minSize = 200.dp)
    ) {
        items(torrents, key = { it.hash }) { torrent ->
            TorrentItem(torrent, onClick = { component.onClickItem(torrent) })
        }
    }
}

@Composable
private fun TorrentItem(
    torrent: Torrent,
    onClick: (Torrent) -> Unit,
    modifier: Modifier = Modifier
) {
    val torrentSize = remember { torrent.size.toReadableSize() }

    Card(modifier = modifier
        .padding(4.dp)
        .clickable { onClick(torrent) }
        .fillMaxSize()
    ) {
        Box(modifier = Modifier.aspectRatio(0.65f)) {
            if (torrent.poster.isEmpty()) {
                AppStubVideo()
            } else {
                AppAsyncImage(
                    modifier = Modifier.fillMaxSize(),
                    url = torrent.poster,
                    contentScale = ContentScale.Crop
                )
            }
        }

        Column(modifier = Modifier.padding(start = 4.dp, end = 4.dp, bottom = 4.dp)) {
            AppNormalText(torrent.title, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Row {
                AppNormalText(stringResource(Res.string.main_torrent_list_files_count))
                Spacer(modifier = Modifier.width(4.dp))
                AppNormalBoldText(torrent.files.size.toString())
                Spacer(modifier = Modifier.weight(1f))
                AppNormalBoldText(torrentSize)
            }
        }
    }
}

@Composable
fun EmptyListStub(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        AppNormalBoldText(stringResource(Res.string.main_torrent_list_is_empty))
    }
}
