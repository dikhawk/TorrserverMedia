package com.dik.torrserverapi.server.mappers

import com.dik.torrserverapi.ContentFile
import com.dik.torrserverapi.model.PlayStatistics
import com.dik.torrserverapi.model.Torrent
import com.dik.torrserverapi.server.response.ContentFileResponse
import com.dik.torrserverapi.server.response.TorrentResponse
import com.dik.torrserverapi.utils.UrlUtils
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

fun TorrentResponse.mapToTorrent(): Torrent = Torrent(
    hash = this.hash ?: "",
    title = this.title ?: "",
    poster = this.poster ?: "",
    name = this.name ?: "",
    files = this.files.toContentFileList(this.hash ?: ""),
    statistics = createPlayStatistics(this),
)

fun List<TorrentResponse>.mapToTorrentList(): List<Torrent> = map { it.mapToTorrent() }

fun ContentFileResponse.maptoContentFile(hash: String): ContentFile = ContentFile(
    id = this.id ?: 0,
    path = this.path ?: "",
    length = this.length ?: 0,
    url = UrlUtils.getPlayLink(
        fileName = this.path?.substringAfterLast("/") ?: "",
        hash = hash,
        index = this.id ?: 0
    )
)

fun List<ContentFileResponse>.mapToConetentFileList(hash: String): List<ContentFile> = map {
    it.maptoContentFile(hash)
}

private fun String?.toContentFileList(hash: String): List<ContentFile> {
    if (this == null) return emptyList()

    val contentFile = Json.decodeFromString<Files>(this)

    return contentFile.torrServer.files.mapToConetentFileList(hash)
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
        torrentSize = torrent.torrentSize ?: 0L,
        preloadedBytes = torrent.preloadedBytes ?: 0L,
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