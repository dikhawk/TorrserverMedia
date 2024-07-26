package com.dik.settings.di

import com.dik.appsettings.api.model.AppSettings
import com.dik.common.AppDispatchers
import com.dik.torrserverapi.server.ServerSettingsApi
import org.koin.core.Koin
import org.koin.dsl.koinApplication
import org.koin.dsl.module

object KoinModules {

    val koin: Koin by lazy {
        koinApplication {

        }.koin
    }

    fun init(dependecies: SettingsDependecies): Koin {
        koin.loadModules(listOf(module {
            single<ServerSettingsApi> { dependecies.torrServerApi().serverSettingsApi() }
            single<AppSettings> { dependecies.appSettings() }
            single<AppDispatchers> { dependecies.dispatchers() }
        }))

        return koin
    }
}

internal inline fun <reified T> inject(): T {
    return KoinModules.koin.get()
}