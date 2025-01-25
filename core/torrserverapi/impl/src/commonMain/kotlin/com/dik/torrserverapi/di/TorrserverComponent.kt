package com.dik.torrserverapi.di

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
                            torrserverComponent = torrserverComponent(dependencies)
                        }
                    }
                }
            }

            return torrserverComponent!!
        }
    }
}

internal expect fun torrserverComponent(dependencies: TorrserverDependencies): TorrserverComponent