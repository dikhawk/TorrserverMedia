package com.dik.torrservermedia.di

import com.dik.torrserverapi.di.TorrserverApi
import com.dik.torrserverapi.di.TorrserverComponent
import org.koin.dsl.module

val torrserverModule = module {
    single<TorrserverApi> { TorrserverComponent.get(get()) }
}