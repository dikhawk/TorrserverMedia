package com.dik.torrentlist.screens.details

import androidx.compose.runtime.Stable
import com.dik.torrentlist.screens.details.files.ContentFilesComponent
import com.dik.torrentlist.screens.details.torrentstatistics.TorrentStatisticsComponent
import com.dik.torrserverapi.model.Torrent
import kotlinx.coroutines.flow.StateFlow

internal interface DetailsComponent {

    val uiState: StateFlow<DetailsState>

    val contentFilesComponent: ContentFilesComponent
    val torrentStatisticsComponent: TorrentStatisticsComponent

    fun showDetails(torrent: Torrent)
}

@Stable
data class DetailsState(
    val poster: String = "",
    val title: String = "",
    val size: String = "",
)