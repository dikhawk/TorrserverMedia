package com.dik.torrentlist.di

import com.dik.torrentlist.DefaultMainTorrentListComponent
import com.dik.torrentlist.MainTorrentListComponent
import com.dik.torrentlist.TorrentListEntry
import com.dik.torrentlist.TorrentListEntryImpl
import com.dik.torrentlist.TorrentListFeatureApi
import com.dik.torrserverapi.di.TorrserverApi
import com.dik.torrserverapi.server.TorrserverStuffApi
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.mp.KoinPlatform

abstract class TorrentListComponent : TorrentListFeatureApi {
    companion object {
        fun initAndGet(dependecies: TorrentListDependecies): TorrentListComponent {

            return object : TorrentListComponent() {
                init {
                    KoinModules.init(dependecies)
                }

                override fun start(): TorrentListEntry {
                    return TorrentListEntryImpl()
                }
            }
        }
    }
}