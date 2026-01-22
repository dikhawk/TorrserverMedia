package com.dik.torrserverapi.di

import com.dik.torrserverapi.model.TorrserverServiceManager
import com.dik.torrserverapi.server.TorrserverCommands
import com.dik.torrserverapi.server.api.MagnetApi
import com.dik.torrserverapi.server.api.ServerSettingsApi
import com.dik.torrserverapi.server.api.TorrentApi
import com.dik.torrserverapi.server.api.TorrserverStuffApi

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
    override fun torrserverServiceManager(): TorrserverServiceManager = inject()

}