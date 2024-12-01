package com.dik.torrentlist.screens.main.torrserverbar

import com.dik.torrentlist.di.inject
import com.dik.torrserverapi.server.TorrserverCommands

internal actual suspend fun startTorserver() {
    val torrserverCommands: TorrserverCommands = inject()
    torrserverCommands.startServer()
}