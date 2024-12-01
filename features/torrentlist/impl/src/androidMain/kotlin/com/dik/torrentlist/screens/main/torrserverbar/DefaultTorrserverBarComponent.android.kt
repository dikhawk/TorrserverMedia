package com.dik.torrentlist.screens.main.torrserverbar

import com.dik.torrentlist.di.inject
import com.dik.torrserverapi.model.TorrserverServiceManager

internal actual suspend fun startTorserver() {
    val torrserverService: TorrserverServiceManager = inject()
    torrserverService.startService()
}