package com.dik.torrentlist.screens.model

import androidx.compose.runtime.Stable

@Stable
internal data class TorrentUiState(
    val hash: String,
    val title: String,
    val poster: String,
    val name: String,
    val size: Long,
    val files: List<ContentFileUiState>,
    val statistics: PlayStatisticsUiState?,
)
