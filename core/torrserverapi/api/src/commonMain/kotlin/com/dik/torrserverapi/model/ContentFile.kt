package com.dik.torrserverapi.model

data class ContentFile(
    val id: Int,
    val path: String,
    val length: Long,
    val url: String,
    val isViewed: Boolean
)