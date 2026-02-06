package com.dik.torrserverapi.di

import com.dik.common.Platform
import com.dik.common.utils.platformName
import com.dik.torrserverapi.server.ServerConfig
import com.dik.torrserverapi.server.ServerConfigLinux
import com.dik.torrserverapi.server.ServerConfigMac
import com.dik.torrserverapi.server.ServerConfigWindows
import com.dik.torrserverapi.server.TorrserverRunner
import com.dik.torrserverapi.server.TorrserverRunnerImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal actual fun platformModule() = module {
    factoryOf(::TorrserverRunnerImpl).bind<TorrserverRunner>()
    factory<ServerConfig> {
        when (platformName()) {
            Platform.LINUX -> ServerConfigLinux()
            Platform.WINDOWS -> ServerConfigWindows()
            Platform.MAC -> ServerConfigMac()
            else -> throw UnsupportedOperationException("Platform not supported ${platformName()}")
        }
    }
}