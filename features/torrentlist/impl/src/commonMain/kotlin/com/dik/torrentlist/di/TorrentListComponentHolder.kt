package com.dik.torrentlist.di

import com.dik.moduleinjector.ComponentHolder
import com.dik.torrentlist.TorrentListFeatureApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

object TorrentListComponentHolder : ComponentHolder<TorrentListFeatureApi, TorrentListDependencies> {

    private var componentHolder: TorrentListComponent? = null
    private val mutex = Mutex()

    override fun init(dependencies: TorrentListDependencies) {
        if (componentHolder == null) {
            runBlocking(Dispatchers.Default) {
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