package com.dik.torrserverapi.server.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ReleaseResponse(
    @SerialName("url") val url: String,
    @SerialName("tag_name") val tagName: String,
    @SerialName("published_at") val publishedAt: String,
    @SerialName("assets") val assets: List<AssetResponse>
)