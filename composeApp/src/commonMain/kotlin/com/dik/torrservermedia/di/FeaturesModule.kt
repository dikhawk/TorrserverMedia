package com.dik.torrservermedia.di

import com.dik.appsettings.api.model.AppSettings
import com.dik.common.AppDispatchers
import com.dik.common.i18n.LocalizationResource
import com.dik.settings.SettingsFeatureApi
import com.dik.settings.di.SettingsComponentHolder
import com.dik.settings.di.SettingsDependencies
import com.dik.torrentlist.TorrentListFeatureApi
import com.dik.torrentlist.di.TorrentListComponentHolder
import com.dik.torrentlist.di.TorrentListDependencies
import com.dik.torrserverapi.di.TorrserverApi
import org.koin.dsl.module

val featuresModule = module {
    factory<TorrentListFeatureApi> {
        TorrentListComponentHolder.init(torrentListDependencies())
        TorrentListComponentHolder.get()
    }

    factory<SettingsFeatureApi> {
        SettingsComponentHolder.init(object : SettingsDependencies {
            override fun torrServerApi(): TorrserverApi = get()
            override fun dispatchers(): AppDispatchers = get()
            override fun appSettings(): AppSettings = get()
            override fun localizationResource(): LocalizationResource = get()
        })
        SettingsComponentHolder.get()
    }
}

internal expect fun torrentListDependencies(): TorrentListDependencies