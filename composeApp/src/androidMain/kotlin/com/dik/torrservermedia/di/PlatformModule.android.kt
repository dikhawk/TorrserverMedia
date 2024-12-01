package com.dik.torrservermedia.di

import com.dik.torrserverapi.di.TorrserverApi
import com.dik.torrserverapi.model.TorrserverServiceManager
import org.koin.dsl.module

internal actual fun platformModule() = module {
    single<TorrserverServiceManager> {
        val torserverApi: TorrserverApi = get()
        torserverApi.torrserverServiceManager()
    }
}