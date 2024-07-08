package com.dik.torrentlist.screens.details.torrentstatistics

import androidx.compose.runtime.Stable
import com.dik.torrentlist.screens.main.torrserverbar.TorrserverBarState
import kotlinx.coroutines.flow.StateFlow

interface TorrentStatisticsComponent {
    val uiState: StateFlow<TorrentStatisticsState>

    fun showStatistics(hash: String)
}

@Stable
data class TorrentStatisticsState(
    val torrentStatus: String = "Unknown status",//stat_string
    val loadedSize: String = "",
    val torrentSize: String = "",
    val preloadedBytes: String = "",
    val downloadSpeed: String = "",
    val uploadSpeed: String = "",
    val totalPeers: String = "",
    val activePeers: String = "",
    val error: String? = null,
)