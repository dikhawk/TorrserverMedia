package com.dik.torrserverapi.di

import com.dik.moduleinjector.BaseApi
import com.dik.torrserverapi.server.TorrserverCommands
import com.dik.torrserverapi.server.api.MagnetApi
import com.dik.torrserverapi.server.api.ServerSettingsApi
import com.dik.torrserverapi.server.api.TorrentApi
import com.dik.torrserverapi.server.api.TorrserverStuffApi

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect interface TorrserverApi : BaseApi {
    fun magnetApi(): MagnetApi
    fun torrentApi(): TorrentApi
    fun torrserverStuffApi(): TorrserverStuffApi
    fun torrserverCommands(): TorrserverCommands
    fun serverSettingsApi(): ServerSettingsApi
}