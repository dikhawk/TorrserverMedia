package com.dik.torrservermedia.di

import com.dik.torrserverapi.di.TorrserverApi
import com.dik.torrserverapi.di.TorrserverComponent
import com.dik.torrserverapi.di.TorrserverDependencies
import org.koin.dsl.module

internal expect fun torrserverDependencies(): TorrserverDependencies

internal val torrserverModule = module {
    single<TorrserverApi> { TorrserverComponent.get(torrserverDependencies()) }
}