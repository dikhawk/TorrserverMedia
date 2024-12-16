package com.dik.torrentlist.screens.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.dik.torrentlist.screens.details.files.ContentFilesUi
import com.dik.torrentlist.screens.details.torrentstatistics.TorrentStatisticsComponent
import com.dik.torrentlist.screens.details.torrentstatistics.TorrentStatisticsUI
import com.dik.uikit.theme.AppTheme
import com.dik.uikit.widgets.AppAsyncImage
import com.dik.uikit.widgets.AppMiddleVerticalSpacer
import com.dik.uikit.widgets.AppNormalBoldText
import com.dik.uikit.widgets.AppNormalHorizontalSpacer
import com.dik.uikit.widgets.AppNormalItalicText
import com.dik.uikit.widgets.AppNormalText
import com.dik.uikit.widgets.AppNormalVerticalSpacer
import org.jetbrains.compose.resources.stringResource
import torrservermedia.features.torrentlist.impl.generated.resources.Res
import torrservermedia.features.torrentlist.impl.generated.resources.main_details_torrent_size

@Composable
internal fun DetailsPaneUi(component: DetailsComponent, modifier: Modifier = Modifier) {
    val uiState by component.uiState.collectAsState()
    val scrollState = rememberScrollState()
    var imageHeight by remember { mutableStateOf(0) }
    var torrentInfoHeight by remember { mutableStateOf(0) }

    Card(modifier = modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier.fillMaxWidth().height(400.dp)
                    .onGloballyPositioned { coordinates ->
                        imageHeight = coordinates.size.height
                    }
            ) {
                AppAsyncImage(
                    modifier = Modifier.fillMaxWidth().blur(25.dp),
                    url = uiState.poster,
                    contentScale = ContentScale.FillWidth,
                )

                AppAsyncImage(
                    url = uiState.poster,
                    modifier = Modifier.fillMaxWidth().align(Alignment.Center),
                    contentScale = ContentScale.Fit,
                )
            }

            Column(
                modifier = Modifier.fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                Spacer(modifier = Modifier.height(with(LocalDensity.current) { imageHeight.toDp() }))

                Column(
                    modifier = Modifier.background(AppTheme.colors.surfaceDim).padding(8.dp)
                ) {
                    TorrentInfo(
                        torrentName = uiState.torrentName,
                        torrentSize = uiState.size,
                        component = component.torrentStatisticsComponent
                    )

                    AppMiddleVerticalSpacer()

                    AboutContentUi(
                        title = uiState.title,
                        overview = uiState.overview,
                        season = uiState.seasonNumber
                    )

                    ContentFilesUi(component = component.contentFilesComponent)
                }
            }

            if (imageHeight <= scrollState.value) {
                AppMiddleVerticalSpacer()
                
                TorrentInfo(
                    modifier = Modifier.background(AppTheme.colors.surfaceDim).padding(8.dp)
                        .onGloballyPositioned { coordinates ->
                            torrentInfoHeight = coordinates.size.height
                        },
                    torrentName = uiState.torrentName,
                    torrentSize = uiState.size,
                    component = component.torrentStatisticsComponent
                )
            }
        }
    }
}

@Composable
private fun TorrentInfo(
    torrentName: String,
    torrentSize: String,
    component: TorrentStatisticsComponent,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        AppNormalText(text = torrentName)
        Row {
            AppNormalItalicText(text = stringResource(Res.string.main_details_torrent_size))
            AppNormalHorizontalSpacer()
            AppNormalBoldText(text = torrentSize)
        }

        TorrentStatisticsUI(component = component)
    }
}