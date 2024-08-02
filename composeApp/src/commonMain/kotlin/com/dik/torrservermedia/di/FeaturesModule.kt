package com.dik.torrservermedia.di

import com.dik.appsettings.api.di.AppSettingsApi
import com.dik.appsettings.api.model.AppSettings
import com.dik.common.AppDispatchers
import com.dik.settings.SettingsFeatureApi
import com.dik.settings.di.SettingsComponentHolder
import com.dik.settings.di.SettingsDependecies
import com.dik.torrentlist.TorrentListFeatureApi
import com.dik.torrentlist.di.TorrentListComponentHolder
import com.dik.torrentlist.di.TorrentListDependecies
import com.dik.torrserverapi.di.TorrserverApi
import org.koin.dsl.module

val featuresModule = module {
    factory<TorrentListFeatureApi> {
        TorrentListComponentHolder.init(object : TorrentListDependecies {
            override fun torrServerApi(): TorrserverApi = get()
            override fun dispatchers(): AppDispatchers = get()
            override fun settingsFeatureApi(): SettingsFeatureApi = get()
            override fun appSettings(): AppSettings = get()
        })
        TorrentListComponentHolder.get()
    }

    factory<SettingsFeatureApi> {
        SettingsComponentHolder.init(object : SettingsDependecies {
            override fun torrServerApi(): TorrserverApi = get()
            override fun dispatchers(): AppDispatchers = get()
            override fun appSettings(): AppSettings = get()
        })
        SettingsComponentHolder.get()
    }
}