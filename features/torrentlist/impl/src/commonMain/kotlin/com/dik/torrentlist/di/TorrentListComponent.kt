package com.dik.torrentlist.di

import com.dik.torrentlist.TorrentListEntry
import com.dik.torrentlist.TorrentListEntryImpl
import com.dik.torrentlist.TorrentListFeatureApi

internal abstract class TorrentListComponent : TorrentListFeatureApi {
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