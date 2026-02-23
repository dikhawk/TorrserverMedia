package com.dik.torrentlist.screens.model

internal data class ContentFileUiState(
    val id: Int,
    val path: String,
    val length: Long,
    val url: String,
    val isViewed: Boolean
)
