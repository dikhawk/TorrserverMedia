package com.dik.torrentlist.screens.main

import com.dik.torrentlist.screens.main.list.TorrentListComponent
import com.dik.torrentlist.screens.main.torrserverbar.TorrserverBarComponent

interface MainComponent {

    fun torrserverBarComponent(): TorrserverBarComponent

    fun torrentListComponent(): TorrentListComponent
}