package com.dik.torrserverapi.di

import com.dik.torrserverapi.model.TorrserverServiceManager
import com.dik.torrserverapi.server.TorrserverRunner
import com.dik.torrserverapi.server.TorrserverRunnerImpl
import com.dik.torrserverapi.service.TorrserverServiceManagerImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal actual fun platformModule() = module {
    factoryOf(::TorrserverServiceManagerImpl).bind<TorrserverServiceManager>()
    factoryOf(::TorrserverRunnerImpl).bind<TorrserverRunner>()
}