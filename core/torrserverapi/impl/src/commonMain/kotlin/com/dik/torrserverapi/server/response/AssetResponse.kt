package com.dik.torrserverapi.server.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AssetResponse(
    @SerialName("name") val name: String,
    @SerialName("browser_download_url") val browserDownloadUrl : String,
    @SerialName("updated_at") val updatedAt: String
)