package com.dik.torrserverapi.server.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ViewedReponse(
    @SerialName("file_index")
    val fileIndex: Int?,
    @SerialName("hash")
    val hash: String?,
)