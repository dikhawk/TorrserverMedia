package com.dik.torrserverapi

data class Torrent(
    val hash: String,
    val title: String,
    val poster: String,
    val name: String,
    val files: List<ContentFile>
)