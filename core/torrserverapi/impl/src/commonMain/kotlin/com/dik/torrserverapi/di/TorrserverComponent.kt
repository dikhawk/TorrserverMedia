package com.dik.torrserverapi.di

import com.dik.common.AppDispatchers
import com.dik.torrserverapi.data.MagnetApi
import com.dik.torrserverapi.data.TorrentApi
import com.dik.torrserverapi.data.TorrserverStuffApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.koin.mp.KoinPlatform

abstract class TorrserverComponent : TorrserverApi {

/*    companion object {
        private var torrserverComponent: TorrserverComponent? = null
        private val mutex = Mutex()

        fun get(appDispatchers: AppDispatchers): TorrserverComponent {
            if (torrserverComponent == null) {
                runBlocking {
                    mutex.withLock {
                        if (torrserverComponent == null) {
                            torrserverComponent = object : TorrserverComponent() {
                                init {
                                    ModuleKoin.init(appDispatchers)
                                }

                                override fun magnetApi(): MagnetApi = KoinPlatform.getKoin().get()
                                override fun torrentApi(): TorrentApi = KoinPlatform.getKoin().get()
                                override fun torrserverStuffApi(): TorrserverStuffApi =
                                    KoinPlatform.getKoin().get()

                            }
                        }
                    }
                }
            }

            return torrserverComponent!!
        }
    }*/

    companion object {
        private val torrserverComponent: TorrserverComponent by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            object : TorrserverComponent() {
                override fun magnetApi(): MagnetApi = KoinPlatform.getKoin().get()
                override fun torrentApi(): TorrentApi = KoinPlatform.getKoin().get()
                override fun torrserverStuffApi(): TorrserverStuffApi =
                    KoinPlatform.getKoin().get()

            }
        }

        fun get(appDispatchers: AppDispatchers): TorrserverComponent {
            ModuleKoin.init(appDispatchers)

            return torrserverComponent
        }
    }
}