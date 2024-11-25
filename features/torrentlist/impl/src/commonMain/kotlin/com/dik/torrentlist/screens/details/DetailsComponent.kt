package com.dik.torrentlist.screens.details

import androidx.compose.runtime.Stable
import com.dik.torrentlist.screens.details.files.ContentFilesComponent
import com.dik.torrentlist.screens.details.torrentstatistics.TorrentStatisticsComponent
import kotlinx.coroutines.flow.StateFlow

internal interface DetailsComponent {

    val uiState: StateFlow<DetailsState>

    val contentFilesComponent: ContentFilesComponent
    val torrentStatisticsComponent: TorrentStatisticsComponent

    fun onClickBack()
    fun showDetails(hash: String)
}

@Stable
data class DetailsState(
    val poster: String = "",
    val filePath: String = "",
    val title: String = "",
    val seasonNumber: String = "",
    val size: String = "",
    val overview: String = "",
)