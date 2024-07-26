package com.dik.torrserverapi.model

data class Release(
    val url: String,
    val tagName: String,
    val publishedAt: String,
    val assets: List<Asset>
)