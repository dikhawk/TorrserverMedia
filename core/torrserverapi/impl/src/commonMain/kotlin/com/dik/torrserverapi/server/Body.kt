package com.dik.torrserverapi.server

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Body(
    @SerialName("action") val action: String = "",
    @SerialName("category") val category: String = "",
    @SerialName("data") val data: String = "",
    @SerialName("hash") val hash: String = "",
    @SerialName("link") val link: String = "",
    @SerialName("poster") val poster: String = "",
    @SerialName("save_to_db") val saveToDb: Boolean = false,
    @SerialName("title") val title: String = "",
)