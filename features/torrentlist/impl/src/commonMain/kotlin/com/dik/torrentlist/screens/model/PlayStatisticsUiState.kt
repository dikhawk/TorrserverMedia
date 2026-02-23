package com.dik.torrentlist.screens.model

internal data class PlayStatisticsUiState(
    val torrentStatus: String,//stat_string
    val loadedSize: Long,
    val preloadSize: Long,
    val preloadedBytes: Long,
    val downloadSpeed: Double,
    val uploadSpeed: Double,
    val totalPeers: Int,
    val activePeers: Int,
    val halfOpenPeers: Int,
    val bytesWritten: Long,
    val bytesRead: Long,
    val bytesReadData: Long,
    val bytesReadUsefulData: Long,
    val chunksRead: Int,
    val chunksReadUseful: Int,
    val chunksReadWasted: Int,
    val piecesDirtiedGood: Int
)
