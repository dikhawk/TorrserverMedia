package com.dik.themoviedb.di

import com.dik.common.AppDispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.dsl.koinApplication
import org.koin.dsl.module

internal object KoinModules {

    private val mutex = Mutex()

    @Volatile
    var koin: Koin? = null
        private set


    fun init(appDispatchers: AppDispatchers) {
        if (koin != null) return

        runBlocking {
            mutex.withLock {
                if (koin == null) {
                    koin = koinApplication { tmdbModules(appDispatchers) }.koin
                }
            }
        }
    }

    private fun KoinApplication.tmdbModules(appDispatchers: AppDispatchers) {
        modules(
            theMovieDbModule,
            httpModule,
            module {
                single<AppDispatchers> { appDispatchers }
            }
        )
    }
}

internal inline fun <reified T> inject(): T {
    return KoinModules.koin!!.get()
}