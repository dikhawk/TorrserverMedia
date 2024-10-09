package com.dik.torrserverapi.server.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.io.File

@Serializable
data class TorrentResponse(
    @SerialName("hash") val hash: String? = "",
    @SerialName("title") val title: String? = "",
    @SerialName("poster") val poster: String? = "",
    @SerialName("name") val name: String? = "",
    @SerialName("data") val files: String? = "",
    @SerialName("stat_string") val torrentStatus: String? = "",
    @SerialName("loaded_size") val loadedSize: Long? = 0L,
    @SerialName("torrent_size") val torrentSize: Long? = 0L,
    @SerialName("preloaded_bytes") val preloadedBytes: Long? = 0L,
    @SerialName("preload_size") val preloadSize: Long? = 0L,
    @SerialName("download_speed") val downloadSpeed: Double? = 0.0,
    @SerialName("upload_speed") val uploadSpeed: Double? = 0.0,
    @SerialName("total_peers") val totalPeers: Int? = 0,
    @SerialName("active_peers") val activePeers: Int? = 0,
    @SerialName("half_open_peers") val halfOpenPeers: Int? = 0,
    @SerialName("bytes_written") val bytesWritten: Long? = 0L,
    @SerialName("bytes_read") val bytesRead: Long? = 0L,
    @SerialName("bytes_read_data") val bytesReadData: Long? = 0L,
    @SerialName("bytes_read_useful_data") val bytesReadUsefulData: Long? = 0L,
    @SerialName("chunks_read") val chunksRead: Int? = 0,
    @SerialName("chunks_read_useful") val chunksReadUseful: Int? = 0,
    @SerialName("chunks_read_wasted") val chunksReadWasted: Int? = 0,
    @SerialName("pieces_dirtied_good") val piecesDirtiedGood: Int? = 0,
    @SerialName("file_stats") val fileStats: List<ContentFileResponse>? = emptyList()
)

@Serializable
data class ContentFileResponse(
    @SerialName("id") val id: Int? = 0,
    @SerialName("path") val path: String? = "",
    @SerialName("length") val length: Long? = 0
)


