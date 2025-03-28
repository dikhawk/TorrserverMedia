package com.dik.torrservermedia.di

import com.dik.appsettings.api.model.AppSettings
import com.dik.common.AppDispatchers
import com.dik.common.i18n.LocalizationResource
import com.dik.common.platform.PlatformEvents
import com.dik.common.platform.WindowAdaptiveClient
import com.dik.settings.SettingsFeatureApi
import com.dik.themoviedb.di.TheMovieDbApi
import com.dik.torrentlist.di.TorrentListDependencies
import com.dik.torrserverapi.di.TorrserverApi

internal actual fun torrentListDependencies() = object : TorrentListDependencies {
    override fun torrServerApi(): TorrserverApi = inject()
    override fun dispatchers(): AppDispatchers = inject()
    override fun settingsFeatureApi(): SettingsFeatureApi = inject()
    override fun appSettings(): AppSettings = inject()
    override fun theMovieDbApi(): TheMovieDbApi = inject()
    override fun platformEvents(): PlatformEvents = inject()
    override fun windowAdaptive(): WindowAdaptiveClient = inject()
    override fun localizationResource(): LocalizationResource = inject()
}