package com.dik.torrentlist.screens.mappers

import com.dik.torrentlist.screens.model.ContentFileUiState
import com.dik.torrentlist.screens.model.PlayStatisticsUiState
import com.dik.torrentlist.screens.model.TorrentUiState
import com.dik.torrserverapi.model.ContentFile
import com.dik.torrserverapi.model.PlayStatistics
import com.dik.torrserverapi.model.Torrent

internal fun Torrent.toTorrentUiState() = TorrentUiState(
    hash = this.hash,
    title = this.title,
    poster = this.poster,
    name = this.name,
    size = this.size,
    files = this.files.toContetFileList(),
    statistics = this.statistics?.toPlayStatisticsUiState()
)

internal fun List<Torrent>.toTorrentUiStateList(): List<TorrentUiState> =
    map { it.toTorrentUiState() }

internal fun ContentFile.toContentFileState() = ContentFileUiState(
    id = this.id,
    path = this.path,
    length = this.length,
    url = this.url,
    isViewed = this.isViewed,
)

internal fun List<ContentFile>.toContetFileList() = map { it.toContentFileState() }

internal fun PlayStatistics.toPlayStatisticsUiState() = PlayStatisticsUiState(
    torrentStatus = this.torrentStatus,
    loadedSize = this.loadedSize,
    preloadSize = this.preloadSize,
    preloadedBytes = this.preloadedBytes,
    downloadSpeed = this.downloadSpeed,
    uploadSpeed = this.uploadSpeed,
    totalPeers = this.totalPeers,
    activePeers = this.activePeers,
    halfOpenPeers = this.halfOpenPeers,
    bytesWritten = this.bytesWritten,
    bytesRead = this.bytesRead,
    bytesReadData = this.bytesReadData,
    bytesReadUsefulData = this.bytesReadUsefulData,
    chunksRead = this.chunksRead,
    chunksReadUseful = this.chunksReadUseful,
    chunksReadWasted = this.chunksReadWasted,
    piecesDirtiedGood = this.piecesDirtiedGood,
)

