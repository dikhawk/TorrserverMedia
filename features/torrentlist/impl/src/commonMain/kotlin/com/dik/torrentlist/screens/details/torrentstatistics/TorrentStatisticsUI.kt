package com.dik.torrentlist.screens.details.torrentstatistics

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dik.uikit.widgets.AppNormalBoldText
import com.dik.uikit.widgets.AppNormalItalicText
import com.dik.uikit.widgets.AppNormalText
import org.jetbrains.compose.resources.stringResource
import torrservermedia.features.torrentlist.impl.generated.resources.Res
import torrservermedia.features.torrentlist.impl.generated.resources.main_statistics_active_peers
import torrservermedia.features.torrentlist.impl.generated.resources.main_statistics_torrent_status
import torrservermedia.features.torrentlist.impl.generated.resources.main_statistics_download_speed

@Composable
fun TorrentStatisticsUI(component: TorrentStatisticsComponent, modifier: Modifier = Modifier) {
    val uiState = component.uiState.collectAsState()

    Column {
        Row {
            AppNormalItalicText(text = stringResource(Res.string.main_statistics_torrent_status))
            Spacer(modifier.width(4.dp))
            AppNormalText(uiState.value.torrentStatus)
        }

        Row {
            AppNormalItalicText(text = stringResource(Res.string.main_statistics_download_speed))
            Spacer(modifier.width(4.dp))
            AppNormalBoldText(uiState.value.downloadSpeed)
        }

        Row {
            AppNormalItalicText(text = stringResource(Res.string.main_statistics_active_peers))
            Spacer(modifier.width(4.dp))
            AppNormalBoldText(uiState.value.activePeers)
        }
    }
}