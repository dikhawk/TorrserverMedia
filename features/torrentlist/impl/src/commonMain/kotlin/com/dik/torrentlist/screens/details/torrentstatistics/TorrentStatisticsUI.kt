package com.dik.torrentlist.screens.details.torrentstatistics

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier

@Composable
fun TorrentStatisticsUI(component: TorrentStatisticsComponent, modifier: Modifier = Modifier) {
    val uiState = component.uiState.collectAsState()

    Column {
        Text("Torrent Status: ${uiState.value.torrentStatus}")
        Text("Download Speed: ${uiState.value.downloadSpeed}")
        Text("Active Peers: ${uiState.value.activePeers}")
    }
}