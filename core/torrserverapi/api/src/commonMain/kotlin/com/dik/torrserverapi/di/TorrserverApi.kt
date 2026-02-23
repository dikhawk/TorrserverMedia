package com.dik.torrserverapi.di

import com.dik.moduleinjector.BaseApi
import com.dik.torrserverapi.server.TorrserverManager
import com.dik.torrserverapi.server.api.ServerSettingsApi
import com.dik.torrserverapi.server.api.TorrentApi
import com.dik.torrserverapi.server.api.TorrserverApiClient

interface TorrserverApi : BaseApi {
    fun torrentApi(): TorrentApi
    fun torrserverApiClient(): TorrserverApiClient
    fun serverSettingsApi(): ServerSettingsApi
    fun torrserverManager(): TorrserverManager
}