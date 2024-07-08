package com.dik.torrserverapi.model

data class PlayStatistics(
    val torrentStatus: String,//stat_string
    val loadedSize: Long,
    val torrentSize: Long,
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