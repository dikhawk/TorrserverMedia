package com.dik.torrentlist.di

import com.arkivanov.decompose.ComponentContext
import com.dik.common.AppDispatchers
import com.dik.moduleinjector.BaseDependencies
import com.dik.torrserverapi.di.TorrserverApi

interface TorrentListDependecies : BaseDependencies {

    fun torrServerApi(): TorrserverApi

    fun dispatchers(): AppDispatchers
}