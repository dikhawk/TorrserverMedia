package com.dik.torrserverapi.di

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

abstract class TorrserverComponent : TorrserverApi {

    companion object {
        private var component: TorrserverComponent? = null
        private val mutex = Mutex()

        fun get(dependencies: TorrserverDependencies): TorrserverComponent {
            if (component == null) {
                runBlocking(Dispatchers.Default) {
                    mutex.withLock {
                        if (component == null) {
                            component = torrserverComponent(dependencies)
                        }
                    }
                }
            }

            return component!!
        }
    }
}

internal expect fun torrserverComponent(dependencies: TorrserverDependencies): TorrserverComponent