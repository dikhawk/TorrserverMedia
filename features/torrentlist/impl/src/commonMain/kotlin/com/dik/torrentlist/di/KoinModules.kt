package com.dik.torrentlist.di

import com.dik.torrserverapi.di.TorrserverApi
import org.koin.core.Koin
import org.koin.dsl.koinApplication
import org.koin.dsl.module

object KoinModules {

    val koin: Koin by lazy {
        koinApplication {

        }.koin
    }

    fun init(dependecies: TorrentListDependecies): Koin {
        koin.loadModules(listOf(module {
            single<TorrserverApi> { dependecies.torrServerApi() }
        }))

        return koin
    }
}

internal inline fun <reified T> inject(): T {
    return KoinModules.koin.get()
}