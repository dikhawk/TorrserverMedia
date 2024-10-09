package com.dik.torrserverapi.server.mappers

import com.dik.torrserverapi.ContentFile
import com.dik.torrserverapi.model.PlayStatistics
import com.dik.torrserverapi.model.Torrent
import com.dik.torrserverapi.model.Viewed
import com.dik.torrserverapi.server.response.ContentFileResponse
import com.dik.torrserverapi.server.response.TorrentResponse
import com.dik.torrserverapi.server.response.ViewedReponse
import com.dik.torrserverapi.utils.UrlUtils
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

internal fun TorrentResponse.mapToTorrent(viewed: List<Viewed> = emptyList()): Torrent = Torrent(
    hash = this.hash ?: "",
    title = this.title ?: "",
    poster = this.poster ?: "",
    name = this.name ?: "",
    files = if (!this.fileStats.isNullOrEmpty())
        this.fileStats.mapToContentFileList(this.hash ?: "", viewed) else
        this.files.toContentFileList(this.hash ?: "", viewed),
    size = this.torrentSize ?: 0L,
    statistics = createPlayStatistics(this),
)

internal fun List<TorrentResponse>.mapToTorrentList(): List<Torrent> = map { it.mapToTorrent() }

internal fun ContentFileResponse.mapToContentFile(
    hash: String,
    viewed: List<Viewed> = emptyList()
): ContentFile = ContentFile(
    id = this.id ?: 0,
    path = this.path ?: "",
    length = this.length ?: 0,
    url = UrlUtils.getPlayLink(
        fileName = this.path?.substringAfterLast("/") ?: "",
        hash = hash,
        index = this.id ?: 0,
    ),
    isViewed = viewed.find { it.id == this.id } != null
)

internal fun List<ContentFileResponse>.mapToContentFileList(
    hash: String,
    viewed: List<Viewed> = emptyList()
): List<ContentFile> = map {
    it.mapToContentFile(hash, viewed)
}

private fun String?.toContentFileList(
    hash: String,
    viewed: List<Viewed> = emptyList()
): List<ContentFile> {
    if (isNullOrEmpty()) return emptyList()

    val contentFile = Json.decodeFromString<Files>(this)

    return contentFile.torrServer.files.mapToContentFileList(hash, viewed)
}

@Serializable
private data class TorrServer(
    @SerialName("Files") val files: List<ContentFileResponse>
)

@Serializable
private data class Files(
    @SerialName("TorrServer") val torrServer: TorrServer
)

private fun createPlayStatistics(torrent: TorrentResponse): PlayStatistics? {
    if (torrent.torrentStatus.isNullOrEmpty()) return null

    return PlayStatistics(
        torrentStatus = torrent.torrentStatus,
        loadedSize = torrent.loadedSize ?: 0L,
        preloadedBytes = torrent.preloadedBytes ?: 0L,
        preloadSize = torrent.preloadSize ?: 0L,
        downloadSpeed = torrent.downloadSpeed ?: 0.0,
        uploadSpeed = torrent.uploadSpeed ?: 0.0,
        totalPeers = torrent.totalPeers ?: 0,
        activePeers = torrent.activePeers ?: 0,
        halfOpenPeers = torrent.halfOpenPeers ?: 0,
        bytesWritten = torrent.bytesWritten ?: 0L,
        bytesRead = torrent.bytesRead ?: 0L,
        bytesReadData = torrent.bytesReadData ?: 0L,
        bytesReadUsefulData = torrent.bytesReadUsefulData ?: 0L,
        chunksRead = torrent.chunksRead ?: 0,
        chunksReadUseful = torrent.chunksReadUseful ?: 0,
        chunksReadWasted = torrent.chunksReadWasted ?: 0,
        piecesDirtiedGood = torrent.piecesDirtiedGood ?: 0,
    )
}

internal fun ViewedReponse.mapToViewed(): Viewed = Viewed(
    id = this.fileIndex ?: 0,
    hash = this.hash ?: ""
)

internal fun List<ViewedReponse>.mapToViewedList(): List<Viewed> {
    return this.map { it.mapToViewed() }
}

