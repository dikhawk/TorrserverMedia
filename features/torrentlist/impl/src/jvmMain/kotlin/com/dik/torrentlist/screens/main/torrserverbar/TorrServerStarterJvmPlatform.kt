package com.dik.torrentlist.screens.main.torrserverbar

import com.dik.torrserverapi.server.TorrserverCommands

class TorrServerStarterJvmPlatform(
    private val torrserverCommands: TorrserverCommands
): TorrServerStarterPlatform {

    override suspend fun startTorrServer() {
        torrserverCommands.startServer()
    }
}