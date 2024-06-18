package com.dik.torrentlist.di

import com.dik.torrentlist.DefaultMainTorrentListComponent
import com.dik.torrentlist.MainTorrentListComponent
import com.dik.torrentlist.TorrentListEntry
import com.dik.torrentlist.TorrentListEntryImpl
import com.dik.torrentlist.TorrentListFeatureApi
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.mp.KoinPlatform

abstract class TorrentListComponent : TorrentListFeatureApi {
    companion object {
        fun initAndGet(dependecies: TorrentListDependecies): TorrentListComponent {

            val torrentListModule = module {
                single { dependecies.torrServerApi() }
            }

            return object : TorrentListComponent() {
                override fun start(): TorrentListEntry {
                    loadKoinModules(torrentListModule)

                    return TorrentListEntryImpl()
                }
            }
        }
    }
}