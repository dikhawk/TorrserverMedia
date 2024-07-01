package com.dik.torrservermedia.di

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.dik.common.AppDispatchers
import com.dik.torrentlist.TorrentListEntry
import com.dik.torrentlist.di.TorrentListComponent
import com.dik.torrentlist.di.TorrentListDependecies
import com.dik.torrserverapi.di.TorrserverApi
import org.koin.dsl.module

val featuresModule = module {
    factory<TorrentListEntry> {
        TorrentListComponent.initAndGet(
            object : TorrentListDependecies {
                override fun torrServerApi(): TorrserverApi = get()
                override fun dispatchers(): AppDispatchers = get()
            }
        ).start()
    }
}