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
actual interface TorrentListDependencies : BaseDependencies {
    actual fun torrServerApi(): TorrserverApi
    actual fun dispatchers(): AppDispatchers
    actual fun settingsFeatureApi(): SettingsFeatureApi
    actual fun appSettings(): AppSettings
    actual fun theMovieDbApi(): TheMovieDbApi
    actual fun platformEvents(): PlatformEvents
    actual fun windowAdaptive(): WindowAdaptiveClient
    actual fun localizationResource(): LocalizationResource
}