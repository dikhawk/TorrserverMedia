package com.dik.torrentlist.screens.main.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dik.torrentlist.converters.toReadableSize
import com.dik.torrserverapi.model.Torrent
import com.dik.uikit.theme.AppTheme
import com.dik.uikit.utils.currentWindowSize
import com.dik.uikit.widgets.AppAsyncImage
import com.dik.uikit.widgets.AppNormalBoldText
import com.dik.uikit.widgets.AppNormalText
import com.dik.uikit.widgets.AppStubVideo
import org.jetbrains.compose.resources.stringResource
import torrservermedia.features.torrentlist.impl.generated.resources.Res
import torrservermedia.features.torrentlist.impl.generated.resources.main_torrent_list_files_count
import torrservermedia.features.torrentlist.impl.generated.resources.main_torrent_list_is_empty

@Composable
internal actual fun TorrentListUi(
    component: TorrentListComponent,
    modifier: Modifier
) {
    val uiState = component.uiState.collectAsState()
    val windowSize = currentWindowSize()

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            uiState.value.torrents.isEmpty() -> EmptyListStub(
                modifier = modifier.align(Alignment.Center)
            )

            else -> Torrents(
                torrents = uiState.value.torrents,
                modifier = modifier,
                onClickItem = { component.onClickItem(it, windowSize) }
            )
        }
    }
}

@Composable
private fun Torrents(
    torrents: List<Torrent>,
    modifier: Modifier = Modifier,
    onClickItem: (Torrent) -> Unit
) {
    LazyVerticalGrid(modifier = modifier, columns = GridCells.Adaptive(150.dp)) {
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
    val torrentSize = remember { torrent.size.toReadableSize() }

    Card(modifier = modifier
        .padding(2.dp)
        .clip(RoundedCornerShape(16.dp, 16.dp, 16.dp, 16.dp))
        .clickable { onClickItem(torrent) }
        .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .aspectRatio(0.65f)
        ) {
            if (torrent.poster.isEmpty()) {
                AppStubVideo()
            } else {
                AppAsyncImage(
                    modifier = Modifier.fillMaxSize(),
                    url = torrent.poster,
                    contentScale = ContentScale.Crop
                )
            }

            Column(
                modifier = Modifier
                    .height(72.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                AppTheme.colors.surface.copy(alpha = 0.7f),
                                AppTheme.colors.surface.copy(alpha = 1.0f)
                            ),
                        ),
                    )
                    .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
                    .align(Alignment.BottomCenter)
            ) {
                AppNormalText(torrent.title, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.weight(1f))
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