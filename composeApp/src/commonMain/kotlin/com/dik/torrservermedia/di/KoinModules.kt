package com.dik.torrservermedia.di

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.koinApplication

object KoinModules {

    private val mutex = Mutex()

    var koin: Koin? = null
        private set

    fun init(config: KoinAppDeclaration? = null) {
        if (koin == null) {
            runBlocking {
                mutex.withLock {
                    startKoin { }
                    koin = koinApplication {
                        config?.invoke(this)
                        appModules()
                    }.koin
                }
            }
        }
    }

    private fun KoinApplication.appModules() {
        modules(
            appModule,
            appSettingsModule,
            featuresModule,
            torrserverModule,
            theMovieDbApiModule,
            platformModule(),
            platformEventsModule(),
            windowAdaptiveModule
        )
    }
}

internal inline fun <reified T> inject(): T {
    return KoinModules.koin!!.get()
}
