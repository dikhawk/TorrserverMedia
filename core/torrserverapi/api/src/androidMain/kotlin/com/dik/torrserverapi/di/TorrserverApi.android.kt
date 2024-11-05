package com.dik.torrserverapi.di

import com.dik.moduleinjector.BaseApi
import com.dik.torrserverapi.model.TorrserverServiceManager
import com.dik.torrserverapi.server.MagnetApi
import com.dik.torrserverapi.server.ServerSettingsApi
import com.dik.torrserverapi.server.TorrentApi
import com.dik.torrserverapi.server.TorrserverCommands
import com.dik.torrserverapi.server.TorrserverStuffApi

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual interface TorrserverApi : BaseApi {
    actual fun magnetApi(): MagnetApi
    actual fun torrentApi(): TorrentApi
    actual fun torrserverStuffApi(): TorrserverStuffApi
    actual fun torrserverCommands(): TorrserverCommands
    actual fun serverSettingsApi(): ServerSettingsApi
    fun torrserverServiceManager(): TorrserverServiceManager
}