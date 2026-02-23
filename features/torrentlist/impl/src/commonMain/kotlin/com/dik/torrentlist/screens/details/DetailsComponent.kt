package com.dik.torrentlist.screens.details

import androidx.compose.runtime.Stable
import com.dik.torrentlist.screens.components.bufferization.BufferizationComponent
import com.dik.torrentlist.screens.details.files.ContentFilesComponent
import com.dik.torrentlist.screens.details.torrentstatistics.TorrentStatisticsComponent
import com.dik.torrentlist.screens.model.ContentFileUiState
import com.dik.torrentlist.screens.model.TorrentUiState
import kotlinx.coroutines.flow.StateFlow

internal interface DetailsComponent {

    val uiState: StateFlow<DetailsState>

    val contentFilesComponent: ContentFilesComponent
    val torrentStatisticsComponent: TorrentStatisticsComponent
    val bufferizationComponent: BufferizationComponent

    fun onClickBack()
    fun onClickDeleteTorrent()
    fun showDetails(hash: String)
    fun runBufferization(
        torrent: TorrentUiState,
        contentFile: ContentFileUiState,
        runAferBuferazation: () -> Unit
    )
}

@Stable
data class DetailsState(
    val poster: String = "",
    val torrentName: String = "",
    val title: String = "",
    val seasonNumber: String = "",
    val size: String = "",
    val overview: String = "",
    val isShowBufferization: Boolean = false
)

internal enum class DetailsComponentScreenFormat {
    PANE,
    SCREEN
}