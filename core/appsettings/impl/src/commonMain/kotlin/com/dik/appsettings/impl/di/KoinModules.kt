package com.dik.appsettings.impl.di

import com.dik.appsettings.api.model.AppSettings
import com.dik.appsettings.impl.AppSettingsImpl
import com.dik.common.AppDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.core.Koin
import org.koin.dsl.koinApplication
import org.koin.dsl.module

object KoinModules {
    val koin: Koin by lazy {
        koinApplication {
//            modules()
        }.koin
    }

    fun init(dependencies: AppSettingsDependencies) {
        koin.loadModules(
            listOf(
                appSettingModule(dependencies),
                module {
                    single<CoroutineScope> {
                        CoroutineScope(
                            SupervisorJob() + dependencies.dispatchers().mainDispatcher()
                        )
                    }
                    factory<AppSettings> { AppSettingsImpl(get()) }
                },
            )
        )
    }
}

internal inline fun <reified T> inject(): T {
    return KoinModules.koin.get()
}