package com.dik.torrserverapi.di

import com.dik.moduleinjector.BaseApi
import com.dik.torrserverapi.server.MagnetApi
import com.dik.torrserverapi.server.TorrentApi
import com.dik.torrserverapi.server.TorrserverStuffApi

interface TorrserverApi: BaseApi {
    fun magnetApi(): MagnetApi
    fun torrentApi(): TorrentApi
    fun torrserverStuffApi(): TorrserverStuffApi
}