package com.dik.torrentlist.di

import com.dik.moduleinjector.ComponentHolder
import com.dik.torrentlist.TorrentListFeatureApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

object TorrentListComponentHolder : ComponentHolder<TorrentListFeatureApi, TorrentListDependecies> {

    private var componentHolder: TorrentListComponent? = null
    private val mutex = Mutex()

    override fun init(dependencies: TorrentListDependecies) {
        if (componentHolder == null) {
            runBlocking {
                mutex.withLock {
                    if (componentHolder == null) {
                        componentHolder = TorrentListComponent.initAndGet(dependencies)
                    }
                }
            }
        }
    }

    override fun get(): TorrentListFeatureApi {
        checkNotNull(componentHolder) { "Component TorrentListComponent not initialized" }
        return componentHolder!!
    }

    override fun reset() {
        componentHolder = null
    }

}