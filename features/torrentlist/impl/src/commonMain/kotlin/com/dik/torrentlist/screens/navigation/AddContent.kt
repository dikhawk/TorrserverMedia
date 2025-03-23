package com.dik.torrentlist.screens.navigation

internal sealed interface AddContent {
    data class Magnet(val magnetLink: String) : AddContent
    data class Torrent(val path: String) : AddContent
}