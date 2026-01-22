package com.dik.torrserverapi.model

import com.dik.torrserverapi.model.ContentFile

data class Torrent(
    val hash: String,
    val title: String,
    val poster: String,
    val name: String,
    val size: Long,
    val files: List<ContentFile>,
    val statistics: PlayStatistics? = null,
)