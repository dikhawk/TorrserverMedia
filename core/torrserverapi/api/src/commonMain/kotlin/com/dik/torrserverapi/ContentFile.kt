package com.dik.torrserverapi

data class ContentFile(
    val id: Int,
    val path: String,
    val length: Long,
    val url: String,
    val isViewved: Boolean
)