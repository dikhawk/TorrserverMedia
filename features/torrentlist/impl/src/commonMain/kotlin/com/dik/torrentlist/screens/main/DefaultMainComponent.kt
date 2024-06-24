package com.dik.torrentlist.screens.main

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.dik.torrentlist.screens.main.list.DefaultTorrentListComponent
import com.dik.torrentlist.screens.main.list.TorrentListComponent
import com.dik.torrentlist.screens.main.torrserverbar.DefaultTorrserverBarComponent
import com.dik.torrentlist.screens.main.torrserverbar.TorrserverBarComponent

class DefaultMainComponent(
    componentContext: ComponentContext
) : MainComponent, ComponentContext by componentContext {
    override fun torrserverBarComponent(): TorrserverBarComponent {
        return DefaultTorrserverBarComponent(childContext("torrserver_bar"))
    }

    override fun torrentListComponent(): TorrentListComponent {
        return DefaultTorrentListComponent(
            context = childContext("torrserverbar"),
            onTorrentClick = {}
        )
    }

}