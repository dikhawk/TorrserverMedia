package com.dik.torrserverapi.di

import com.dik.common.Platform
import com.dik.common.utils.platformName
import com.dik.torrserverapi.data.SystemProcessProviderLinux
import com.dik.torrserverapi.data.SystemProcessProviderWindows
import com.dik.torrserverapi.server.ServerConfig
import com.dik.torrserverapi.server.ServerConfigLinux
import com.dik.torrserverapi.server.ServerConfigMac
import com.dik.torrserverapi.server.ServerConfigWindows
import com.dik.torrserverapi.server.TorrserverRunner
import com.dik.torrserverapi.server.TorrserverRunnerLinux
import com.dik.torrserverapi.server.TorrserverRunnerWindows
import org.koin.dsl.module

internal actual fun platformModule() = module {
    factory<ServerConfig> {
        when (platformName()) {
            Platform.LINUX -> ServerConfigLinux()
            Platform.WINDOWS -> ServerConfigWindows()
            Platform.MAC -> ServerConfigMac()
            else -> throw UnsupportedOperationException("Platform not supported ${platformName()}")
        }
    }
    factory<TorrserverRunner> {
        when (platformName()) {
            Platform.LINUX -> TorrserverRunnerLinux(get(), get(), get(), get())
            Platform.WINDOWS -> TorrserverRunnerWindows(get(), get(), get(), get())
            else -> throw UnsupportedOperationException("Platform not supported ${platformName()}")
        }
    }
    factory {
        when (platformName()) {
            Platform.LINUX -> SystemProcessProviderLinux(get())
            Platform.WINDOWS -> SystemProcessProviderWindows(get())
            else -> throw UnsupportedOperationException("Platform not supported ${platformName()}")
        }
    }
}