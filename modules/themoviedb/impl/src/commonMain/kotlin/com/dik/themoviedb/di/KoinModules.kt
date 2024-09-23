package com.dik.themoviedb.di

import com.dik.common.AppDispatchers
import org.koin.core.Koin
import org.koin.dsl.koinApplication
import org.koin.dsl.module

internal object KoinModules {
    val koin: Koin by lazy {
        koinApplication {
            modules(theMovieDbModule, httpModule, theMovieDbModule)
        }.koin
    }

    fun init(appDispatchers: AppDispatchers) {
        koin.loadModules(
            listOf(
                module {
                    single<AppDispatchers> { appDispatchers }
                }
            )
        )
    }
}

internal inline fun <reified T> inject(): T {
    return KoinModules.koin.get()
}