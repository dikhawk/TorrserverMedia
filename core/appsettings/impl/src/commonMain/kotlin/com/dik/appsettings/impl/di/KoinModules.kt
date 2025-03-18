package com.dik.appsettings.impl.di

import com.dik.appsettings.api.model.AppSettings
import com.dik.appsettings.impl.AppSettingsImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
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

    fun init(dependencies: AppSettingsDependencies) {
        if (koin != null) return

        runBlocking {
            mutex.withLock {
                if (koin == null) {
                    koin = koinApplication { appsettingsModules(dependencies) }.koin
                }
            }
        }
    }

    private fun KoinApplication.appsettingsModules(dependencies: AppSettingsDependencies)  {
        modules(
            appSettingModule(dependencies),
            commonModule(dependencies)
        )
    }
}

internal fun commonModule(dependencies: AppSettingsDependencies) = module {
    single<CoroutineScope> {
        CoroutineScope(SupervisorJob() + dependencies.dispatchers().mainDispatcher())
    }
    factory<AppSettings> { AppSettingsImpl(get()) }
}

internal inline fun <reified T> inject(): T {
    return KoinModules.koin!!.get()
}