package com.dik.torrentlist.screens.components.bufferization

import androidx.compose.runtime.Stable
import com.dik.torrentlist.screens.model.ContentFileUiState
import com.dik.torrentlist.screens.model.TorrentUiState
import kotlinx.coroutines.flow.StateFlow

internal interface BufferizationComponent {
    val uiState: StateFlow<BufferizationState>

    fun startBufferization(
        torrent: TorrentUiState,
        contentFile: ContentFileUiState,
        runAfterBufferization: () -> Unit
    )

    fun onClickCancel()
}

@Stable
internal data class BufferizationState(
    val fileName: String = "",
    val title: String = "",
    val titleSecond: String = "",
    val fileSize: String = "",
    val totalPeers: String = "",
    val activePeers: String = "",
    val downloadSpeed: String = "",
    val uploadSpeed: String = "",
    val downloadProgress: Float = 0.0f,
    val downloadProgressText: String = "",
    val overview: String = "",
)