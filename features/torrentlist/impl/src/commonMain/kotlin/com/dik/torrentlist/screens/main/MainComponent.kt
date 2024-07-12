package com.dik.torrentlist.screens.main

import com.dik.torrentlist.screens.details.DetailsComponent
import com.dik.torrentlist.screens.main.appbar.MainAppBarComponent
import com.dik.torrentlist.screens.main.list.TorrentListComponent
import com.dik.torrentlist.screens.main.torrserverbar.TorrserverBarComponent

internal interface MainComponent {

    val mainAppBarComponent: MainAppBarComponent

    val torrserverBarComponent: TorrserverBarComponent

    val torrentListComponent: TorrentListComponent

    val detailsComponent: DetailsComponent
}