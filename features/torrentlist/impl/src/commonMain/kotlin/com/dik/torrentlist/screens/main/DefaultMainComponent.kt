package com.dik.torrentlist.screens.main

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.dik.appsettings.api.model.AppSettings
import com.dik.common.AppDispatchers
import com.dik.common.cmd.CmdRunner
import com.dik.common.player.PlayersCommands
import com.dik.common.player.platformPlayersCommands
import com.dik.torrentlist.di.inject
import com.dik.torrentlist.screens.details.DefaultDetailsComponent
import com.dik.torrentlist.screens.main.appbar.DefaultMainAppBarComponent
import com.dik.torrentlist.screens.main.appbar.MainAppBarComponent
import com.dik.torrentlist.screens.main.list.DefaultTorrentListComponent
import com.dik.torrentlist.screens.main.list.TorrentListComponent
import com.dik.torrentlist.screens.main.torrserverbar.DefaultTorrserverBarComponent
import com.dik.torrentlist.screens.main.torrserverbar.TorrserverBarComponent
import com.dik.torrserverapi.server.MagnetApi
import com.dik.torrserverapi.server.TorrentApi
import com.dik.torrserverapi.server.TorrserverCommands
import com.dik.torrserverapi.server.TorrserverStuffApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

internal class DefaultMainComponent(
    context: ComponentContext,
    private val torrentApi: TorrentApi = inject(),
    private val magnetApi: MagnetApi = inject(),
    private val dispatchers: AppDispatchers = inject(),
    private val cmdRunner: CmdRunner = inject(),
    private val openSettingsScreen: () -> Unit = {},
    private val appSettings: AppSettings = inject(),
    private val torrserverStuffApi: TorrserverStuffApi = inject(),
    private val torrserverCommands: TorrserverCommands = inject(),
    private val playersCommands: PlayersCommands = platformPlayersCommands()
) : MainComponent, ComponentContext by context {

    private val componentScope = CoroutineScope(dispatchers.mainDispatcher() + SupervisorJob())

    init {
        lifecycle.doOnDestroy { componentScope.cancel() }
    }

    override val mainAppBarComponent: MainAppBarComponent = DefaultMainAppBarComponent(
        context = childContext("main_app_bar"),
        dispatchers = dispatchers,
        torrentApi = torrentApi,
        magnetApi = magnetApi,
        openSettingsScreen = openSettingsScreen,
        torrserverStuffApi = torrserverStuffApi,
    )

    override val torrserverBarComponent: TorrserverBarComponent =
        DefaultTorrserverBarComponent(
            context = childContext("torrserver_bar"),
            torrserverStuffApi = torrserverStuffApi,
            torrserverCommands = torrserverCommands,
            dispatchers = dispatchers
        )

    override val torrentListComponent: TorrentListComponent = DefaultTorrentListComponent(
        context = childContext("torrserverbar"),
        onTorrentClick = { torrent ->
            if (torrent.files.size == 1) {
                val contentFile = torrent.files.first()
                componentScope.launch(dispatchers.defaultDispatcher()) {
                    playersCommands.playFile(
                        fileName = contentFile.path,
                        fileUrl = contentFile.url,
                        player = appSettings.defaultPlayer
                    )
                }
            }
            detailsComponent.showDetails(torrent)
        },
        torrentApi = torrentApi,
        torrserverCommands = torrserverCommands,
        dispatchers = dispatchers
    )

    override val detailsComponent = DefaultDetailsComponent(childContext(("details")))
}