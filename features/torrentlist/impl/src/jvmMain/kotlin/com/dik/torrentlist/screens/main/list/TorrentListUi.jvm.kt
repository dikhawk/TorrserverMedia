package com.dik.torrentlist.screens.main.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.DragData
import androidx.compose.ui.draganddrop.dragData
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dik.torrentlist.converters.toReadableSize
import com.dik.torrserverapi.model.Torrent
import com.dik.uikit.utils.currentWindowSize
import com.dik.uikit.widgets.AppAsyncImage
import com.dik.uikit.widgets.AppNormalBoldText
import com.dik.uikit.widgets.AppNormalText
import com.dik.uikit.widgets.AppStubVideo
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import torrservermedia.features.torrentlist.impl.generated.resources.Res
import torrservermedia.features.torrentlist.impl.generated.resources.ic_cross
import torrservermedia.features.torrentlist.impl.generated.resources.ic_torrent
import torrservermedia.features.torrentlist.impl.generated.resources.main_torrent_list_add_torrent
import torrservermedia.features.torrentlist.impl.generated.resources.main_torrent_list_files_count
import torrservermedia.features.torrentlist.impl.generated.resources.main_torrent_list_is_empty

@Composable
internal actual fun TorrentListUi(
    component: TorrentListComponent,
    modifier: Modifier
) {
    val uiState = component.uiState.collectAsState()

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        DragAndDropFile(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
            onDropFiles = { files -> component.addTorrents(files) }) {
            when {
                uiState.value.torrents.isEmpty() -> EmptyListStub()
                else -> Torrents(
                    uiState.value.torrents,
                    component
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
private fun DragAndDropFile(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    onDropFiles: (List<String>) -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    var isShowDragAndDrop by remember { mutableStateOf(false) }

    Box(
        modifier = modifier.dragAndDropTarget(
            shouldStartDragAndDrop = {
                isShowDragAndDrop = true
                true
            },
            target = remember {
                object : DragAndDropTarget {
                    override fun onDrop(event: DragAndDropEvent): Boolean {
                        val dragData = event.dragData()
                        if (dragData is DragData.FilesList) {
                            try {
                                onDropFiles(dragData.readFiles())
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        return false
                    }

                    override fun onEnded(event: DragAndDropEvent) {
                        isShowDragAndDrop = false
                        super.onEnded(event)
                    }

                    override fun onExited(event: DragAndDropEvent) {
                        isShowDragAndDrop = false
                        super.onExited(event)
                    }
                }
            }
        ),
        contentAlignment = contentAlignment,
    ) {
        content()
        AnimatedVisibility(
            visible = isShowDragAndDrop,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Card(
                modifier = Modifier.height(200.dp).width(200.dp).align(Alignment.Center)
                    .padding(8.dp)
            ) {
                Box(modifier = Modifier.padding(8.dp).fillMaxSize()) {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            modifier = Modifier.size(width = 100.dp, height = 100.dp),
                            painter = painterResource(Res.drawable.ic_torrent),
                            contentDescription = null
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        AppNormalBoldText(
                            text = stringResource(Res.string.main_torrent_list_add_torrent),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Torrents(
    torrents: List<Torrent>,
    component: TorrentListComponent,
    modifier: Modifier = Modifier
) {
    val windowSize = currentWindowSize()

    LazyVerticalGrid(
        modifier = modifier.fillMaxSize(),
        columns = GridCells.Adaptive(minSize = 180.dp)
    ) {
        items(torrents, key = { it.hash }) { torrent ->
            TorrentItem(
                torrent,
                onClickItem = { component.onClickItem(torrent, windowSize) },
                onClickDelete = { component.onClickDeleteItem(torrent) })
        }
    }
}

@Composable
private fun TorrentItem(
    torrent: Torrent,
    onClickItem: (Torrent) -> Unit,
    onClickDelete: (Torrent) -> Unit,
    modifier: Modifier = Modifier
) {
    val torrentSize = remember { torrent.size.toReadableSize() }
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    Card(
        modifier = modifier
            .padding(4.dp)
            .clip(RoundedCornerShape(15.dp, 15.dp, 15.dp, 15.dp))
            .clickable { onClickItem(torrent) }
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .aspectRatio(0.65f)
                .hoverable(interactionSource)
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
            Column(modifier = Modifier.align(Alignment.TopEnd)) {
                AnimatedVisibility(
                    visible = isHovered,
                    enter = scaleIn() + fadeIn(),
                    exit = scaleOut() + fadeOut()
                ) {
                    Image(
                        imageVector = vectorResource(Res.drawable.ic_cross),
                        modifier = Modifier.size(48.dp).clickable { onClickDelete(torrent) }
                            .padding(4.dp),
                        contentDescription = null
                    )
                }
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