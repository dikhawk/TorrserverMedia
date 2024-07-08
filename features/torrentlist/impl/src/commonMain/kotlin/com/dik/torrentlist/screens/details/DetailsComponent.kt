package com.dik.torrentlist.screens.details

import com.dik.torrentlist.screens.details.files.ContentFilesComponent
import com.dik.torrentlist.screens.details.torrentstatistics.TorrentStatisticsComponent
import com.dik.torrserverapi.model.Torrent

internal interface DetailsComponent {

    val contentFilesComponent: ContentFilesComponent
    val torrentStatisticsComponent: TorrentStatisticsComponent

    fun showDetails(torrent: Torrent)
}