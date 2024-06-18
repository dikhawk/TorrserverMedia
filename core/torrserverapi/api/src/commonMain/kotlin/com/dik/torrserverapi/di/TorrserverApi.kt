package com.dik.torrserverapi.di

import com.dik.common.Result
import com.dik.moduleinjector.BaseApi
import com.dik.torrserverapi.data.MagnetApi
import com.dik.torrserverapi.data.TorrentApi
import com.dik.torrserverapi.data.TorrserverStuffApi

interface TorrserverApi: BaseApi {
    fun magnetApi(): MagnetApi
    fun torrentApi(): TorrentApi
    fun torrserverStuffApi(): TorrserverStuffApi
}