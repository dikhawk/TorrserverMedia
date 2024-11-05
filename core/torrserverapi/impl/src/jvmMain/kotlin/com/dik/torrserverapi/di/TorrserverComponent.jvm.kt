package com.dik.torrserverapi.di

import com.dik.torrserverapi.server.MagnetApi
import com.dik.torrserverapi.server.ServerSettingsApi
import com.dik.torrserverapi.server.TorrentApi
import com.dik.torrserverapi.server.TorrserverCommands
import com.dik.torrserverapi.server.TorrserverStuffApi

internal actual fun torrserverComponent(
    dependencies: TorrserverDependencies
) = object : TorrserverComponent() {
    init {
        KoinModules.init(dependencies)
    }

    override fun magnetApi(): MagnetApi = inject()
    override fun torrentApi(): TorrentApi = inject()
    override fun torrserverStuffApi(): TorrserverStuffApi = inject()
    override fun torrserverCommands(): TorrserverCommands = inject()
    override fun serverSettingsApi(): ServerSettingsApi = inject()
}