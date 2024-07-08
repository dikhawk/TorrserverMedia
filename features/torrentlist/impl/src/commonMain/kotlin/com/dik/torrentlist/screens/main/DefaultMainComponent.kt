package com.dik.torrentlist.screens.main

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.dik.common.AppDispatchers
import com.dik.torrentlist.di.inject
import com.dik.torrentlist.screens.details.DefaultDetailsComponent
import com.dik.torrentlist.screens.details.DetailsComponent
import com.dik.torrentlist.screens.main.list.DefaultTorrentListComponent
import com.dik.torrentlist.screens.main.list.TorrentListComponent
import com.dik.torrentlist.screens.main.torrserverbar.DefaultTorrserverBarComponent
import com.dik.torrentlist.screens.main.torrserverbar.TorrserverBarComponent
import com.dik.torrserverapi.server.TorrentApi

internal class DefaultMainComponent(
    componentContext: ComponentContext,
    val torrentApi: TorrentApi = inject(),
    val dispatchers: AppDispatchers = inject(),
) : MainComponent, ComponentContext by componentContext {

    override fun torrserverBarComponent(): TorrserverBarComponent {
        return DefaultTorrserverBarComponent(childContext("torrserver_bar"))
    }

    override fun torrentListComponent(): TorrentListComponent {
        return DefaultTorrentListComponent(
            context = childContext("torrserverbar"),
            onTorrentClick = { detailsComponent.showDetails(it) },
            torrentApi = torrentApi,
            dispatchers = dispatchers
        )
    }

    override val detailsComponent = DefaultDetailsComponent(childContext(("details")))
}