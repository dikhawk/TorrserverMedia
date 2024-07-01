package com.dik.torrserverapi.server.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TorrentResponse(
    @SerialName("hash") val hash: String? = "",
    @SerialName("title") val title: String? = "",
    @SerialName("poster") val poster: String? = "",
    @SerialName("name") val name: String? = "",
    @SerialName("file_stats") val fileStats: List<ContentFileResponse> = emptyList()
)

@Serializable
data class ContentFileResponse(
    @SerialName("id") val id: Int? = 0,
    @SerialName("path") val path: String? = "",
    @SerialName("length") val length: Long? = 0
)

