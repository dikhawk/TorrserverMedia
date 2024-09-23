package com.dik.torrentlist.di

import com.dik.torrentlist.TorrentListEntry
import com.dik.torrentlist.TorrentListEntryImpl
import com.dik.torrentlist.TorrentListFeatureApi

internal abstract class TorrentListComponent : TorrentListFeatureApi {
    companion object {
        fun initAndGet(dependencies: TorrentListDependencies): TorrentListComponent {

            return object : TorrentListComponent() {
                init {
                    KoinModules.init(dependencies)
                }

                override fun start(): TorrentListEntry {
                    return TorrentListEntryImpl()
                }
            }
        }
    }
}