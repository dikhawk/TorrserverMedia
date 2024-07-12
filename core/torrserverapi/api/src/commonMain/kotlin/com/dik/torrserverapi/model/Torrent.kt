package com.dik.torrserverapi.model

import com.dik.torrserverapi.ContentFile

data class Torrent(
    val hash: String,
    val title: String,
    val poster: String,
    val name: String,
    val size: Long,
    val files: List<ContentFile>,
    val statistics: PlayStatistics? = null,
)