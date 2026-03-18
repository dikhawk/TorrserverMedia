package com.dik.torrserverapi.di

import com.dik.torrserverapi.SystemProcessProviderAndroid
import com.dik.torrserverapi.domain.SystemProcessProvider
import com.dik.torrserverapi.model.TorrserverServiceManager
import com.dik.torrserverapi.server.ServerConfig
import com.dik.torrserverapi.server.ServerConfigAndroid
import com.dik.torrserverapi.server.TorrserverRunner
import com.dik.torrserverapi.server.TorrserverRunnerAndroid
import com.dik.torrserverapi.service.TorrserverServiceManagerImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal actual fun platformModule() = module {
    factoryOf(::TorrserverServiceManagerImpl).bind<TorrserverServiceManager>()
    factoryOf(::TorrserverRunnerAndroid).bind<TorrserverRunner>()
    factoryOf(::ServerConfigAndroid).bind<ServerConfig>()
    factoryOf(::SystemProcessProviderAndroid).bind<SystemProcessProvider>()
}