package com.dik.torrentlist.di

import com.dik.appsettings.api.model.AppSettings
import com.dik.common.AppDispatchers
import com.dik.moduleinjector.BaseDependencies
import com.dik.settings.SettingsFeatureApi
import com.dik.themoviedb.di.TheMovieDbApi
import com.dik.torrserverapi.di.TorrserverApi

interface TorrentListDependencies : BaseDependencies {

    fun torrServerApi(): TorrserverApi

    fun dispatchers(): AppDispatchers

    fun settingsFeatureApi(): SettingsFeatureApi

    fun appSettings(): AppSettings

    fun theMovieDbApi(): TheMovieDbApi
}