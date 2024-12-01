package com.dik.torrentlist.di

import android.content.Context
import com.dik.appsettings.api.model.AppSettings
import com.dik.common.AppDispatchers
import com.dik.moduleinjector.BaseDependencies
import com.dik.settings.SettingsFeatureApi
import com.dik.themoviedb.di.TheMovieDbApi
import com.dik.torrserverapi.di.TorrserverApi
import com.dik.torrserverapi.model.TorrserverServiceManager

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual interface TorrentListDependencies : BaseDependencies {
    actual fun torrServerApi(): TorrserverApi
    actual fun dispatchers(): AppDispatchers
    actual fun settingsFeatureApi(): SettingsFeatureApi
    actual fun appSettings(): AppSettings
    actual fun theMovieDbApi(): TheMovieDbApi
    fun context(): Context
    fun torrserverServiceManager(): TorrserverServiceManager
}