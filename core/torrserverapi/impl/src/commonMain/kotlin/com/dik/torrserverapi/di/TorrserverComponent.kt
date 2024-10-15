package com.dik.torrserverapi.di

import com.dik.common.AppDispatchers
import com.dik.torrserverapi.server.MagnetApi
import com.dik.torrserverapi.server.ServerSettingsApi
import com.dik.torrserverapi.server.TorrserverCommands
import com.dik.torrserverapi.server.TorrentApi
import com.dik.torrserverapi.server.TorrserverStuffApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

abstract class TorrserverComponent : TorrserverApi {

    companion object {
        private var torrserverComponent: TorrserverComponent? = null
        private val mutex = Mutex()

        fun get(dependencies: TorrserverDependencies): TorrserverComponent {
            if (torrserverComponent == null) {
                runBlocking {
                    mutex.withLock {
                        if (torrserverComponent == null) {
                            torrserverComponent = object : TorrserverComponent() {
                                init {
                                    KoinModules.init(dependencies)
                                }

                                override fun magnetApi(): MagnetApi = inject()
                                override fun torrentApi(): TorrentApi = inject()
                                override fun torrserverStuffApi(): TorrserverStuffApi = inject()
                                override fun torrserverCommands(): TorrserverCommands = inject()
                                override fun serverSettingsApi(): ServerSettingsApi = inject()
                            }
                        }
                    }
                }
            }

            return torrserverComponent!!
        }
    }
}