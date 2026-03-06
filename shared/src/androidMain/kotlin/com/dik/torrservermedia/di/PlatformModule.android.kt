package com.dik.torrservermedia.di

import com.dik.torrserverapi.di.TorrserverApi
import com.dik.torrserverapi.server.TorrserverManager
import org.koin.dsl.module

internal actual fun platformModule() = module {
    single<TorrserverManager> {
        get<TorrserverApi>().torrserverManager()
    }
}