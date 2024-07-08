package com.dik.torrentlist.screens.main

import com.dik.torrentlist.screens.details.DetailsComponent
import com.dik.torrentlist.screens.main.list.TorrentListComponent
import com.dik.torrentlist.screens.main.torrserverbar.TorrserverBarComponent

internal interface MainComponent {

    fun torrserverBarComponent(): TorrserverBarComponent

    fun torrentListComponent(): TorrentListComponent

    val detailsComponent: DetailsComponent
}