package com.dik.torrservermedia.di

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
        })
        TorrentListComponentHolder.get()
    }

    factory<SettingsFeatureApi> {
        SettingsComponentHolder.init(object : SettingsDependecies {})
        SettingsComponentHolder.get()
    }
}