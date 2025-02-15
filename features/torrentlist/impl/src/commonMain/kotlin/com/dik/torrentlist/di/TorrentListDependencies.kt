package com.dik.torrentlist.di

import com.dik.appsettings.api.model.AppSettings
import com.dik.common.AppDispatchers
import com.dik.common.i18n.LocalizationResource
import com.dik.common.platform.PlatformEvents
import com.dik.common.platform.WindowAdaptiveClient
import com.dik.moduleinjector.BaseDependencies
import com.dik.settings.SettingsFeatureApi
import com.dik.themoviedb.di.TheMovieDbApi
import com.dik.torrserverapi.di.TorrserverApi

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect interface TorrentListDependencies : BaseDependencies {

    fun torrServerApi(): TorrserverApi

    fun dispatchers(): AppDispatchers

    fun settingsFeatureApi(): SettingsFeatureApi

    fun appSettings(): AppSettings

    fun theMovieDbApi(): TheMovieDbApi

    fun platformEvents(): PlatformEvents

    fun windowAdaptive(): WindowAdaptiveClient

    fun localizationResource(): LocalizationResource
}