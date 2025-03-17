package com.dik.settings.di

import com.dik.appsettings.api.model.AppSettings
import com.dik.common.AppDispatchers
import com.dik.common.i18n.LocalizationResource
import com.dik.torrserverapi.server.ServerSettingsApi
import com.dik.torrserverapi.server.TorrserverCommands
import com.dik.torrserverapi.server.TorrserverStuffApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.dsl.koinApplication
import org.koin.dsl.module

object KoinModules {

    private val mutex = Mutex()

    @Volatile
    var koin: Koin? = null
        private set

    fun init(dependencies: SettingsDependencies) {
        if (koin != null) return

        runBlocking {
            mutex.withLock {
                if (koin == null) {
                    koin = koinApplication { settingsListModules(dependencies) }.koin
                }
            }
        }
    }
}

internal fun KoinApplication.settingsListModules(dependencies: SettingsDependencies) {
    modules(
        module {
            single<ServerSettingsApi> { dependencies.torrServerApi().serverSettingsApi() }
            single<AppSettings> { dependencies.appSettings() }
            single<AppDispatchers> { dependencies.dispatchers() }
            single<TorrserverStuffApi> { dependencies.torrServerApi().torrserverStuffApi() }
            single<TorrserverCommands> { dependencies.torrServerApi().torrserverCommands() }
            single<LocalizationResource> { dependencies.localizationResource() }
        }
    )
}

internal inline fun <reified T> inject(): T {
    return KoinModules.koin!!.get()
}