package com.dik.torrserverapi.di

import com.dik.moduleinjector.BaseApi
import com.dik.torrserverapi.server.MagnetApi
import com.dik.torrserverapi.server.ServerSettingsApi
import com.dik.torrserverapi.server.TorrentApi
import com.dik.torrserverapi.server.TorrserverCommands
import com.dik.torrserverapi.server.TorrserverStuffApi

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect interface TorrserverApi : BaseApi {
    fun magnetApi(): MagnetApi
    fun torrentApi(): TorrentApi
    fun torrserverStuffApi(): TorrserverStuffApi
    fun torrserverCommands(): TorrserverCommands
    fun serverSettingsApi(): ServerSettingsApi
}