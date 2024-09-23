package com.dik.torrentlist.screens.components.bufferization

import androidx.compose.runtime.Stable
import com.dik.torrserverapi.ContentFile
import com.dik.torrserverapi.model.Torrent
import kotlinx.coroutines.flow.StateFlow

internal interface BufferizationComponent {
    val uiState: StateFlow<BufferizationState>

    fun startBufferezation(torrent: Torrent, contentFile: ContentFile, runAferBuferazation: () -> Unit)
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