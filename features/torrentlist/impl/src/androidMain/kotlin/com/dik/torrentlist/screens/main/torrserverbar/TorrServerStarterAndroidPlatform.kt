package com.dik.torrentlist.screens.main.torrserverbar

import com.dik.torrserverapi.model.TorrserverServiceManager

class TorrServerStarterAndroidPlatform(
    private val torrserverService: TorrserverServiceManager
): TorrServerStarterPlatform {

    override suspend fun startTorrServer() {
        torrserverService.startService()
    }
}