package com.dik.torrentlist.screens.details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.dik.torrentlist.screens.details.files.ContentFilesUi
import com.dik.torrentlist.screens.details.torrentstatistics.TorrentStatisticsUI
import com.dik.uikit.widgets.AppAsyncImage
import com.dik.uikit.widgets.AppNormalBoldText
import com.dik.uikit.widgets.AppNormalItalicText
import com.dik.uikit.widgets.AppNormalText
import com.dik.uikit.widgets.AppStubVideo
import org.jetbrains.compose.resources.stringResource
import torrservermedia.features.torrentlist.impl.generated.resources.Res
import torrservermedia.features.torrentlist.impl.generated.resources.main_details_torrent_size

@Composable
internal fun DetailsPaneUi(component: DetailsComponent, modifier: Modifier = Modifier) {
    val uiState = component.uiState.collectAsState()

    Card(modifier = modifier.fillMaxSize()) {
        Row {
            Box(modifier = Modifier.height(180.dp).width(120.dp)) {
                if (uiState.value.poster.isEmpty()) {
                    AppStubVideo()
                } else {
                    AppAsyncImage(
                        url = uiState.value.poster,
                        contentScale = ContentScale.Fit,
                    )
                }
            }
            Column(modifier = Modifier.padding(8.dp)) {
                AppNormalText(text = uiState.value.filePath)
                Row {
                    AppNormalItalicText(text = stringResource(Res.string.main_details_torrent_size))
                    Spacer(modifier = Modifier.width(8.dp))
                    AppNormalBoldText(text = uiState.value.size)
                }
                TorrentStatisticsUI(component = component.torrentStatisticsComponent)
            }
        }

        ContentFilesUi(
            title = uiState.value.title,
            overview = uiState.value.overview,
            season = uiState.value.seasonNumber,
            component = component.contentFilesComponent,
            modifier = Modifier.padding(8.dp)
        )
    }
}