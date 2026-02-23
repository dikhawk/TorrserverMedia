package com.dik.torrserverapi.di

import com.dik.torrserverapi.server.TorrserverManager
import com.dik.torrserverapi.server.api.ServerSettingsApi
import com.dik.torrserverapi.server.api.TorrentApi
import com.dik.torrserverapi.server.api.TorrserverApiClient

internal actual fun torrserverComponent(
    dependencies: TorrserverDependencies
) = object : TorrserverComponent() {
    init {
        KoinModules.init(dependencies)
    }

    override fun torrentApi(): TorrentApi = inject()
    override fun torrserverApiClient(): TorrserverApiClient = inject()
    override fun serverSettingsApi(): ServerSettingsApi = inject()
    override fun torrserverManager(): TorrserverManager = inject()
}