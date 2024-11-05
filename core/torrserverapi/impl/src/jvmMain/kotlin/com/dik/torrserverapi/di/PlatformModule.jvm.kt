package com.dik.torrserverapi.di

import com.dik.torrserverapi.server.TorrserverRunner
import com.dik.torrserverapi.server.TorrserverRunnerImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal actual fun platformModule() = module {
    factoryOf(::TorrserverRunnerImpl).bind<TorrserverRunner>()
}