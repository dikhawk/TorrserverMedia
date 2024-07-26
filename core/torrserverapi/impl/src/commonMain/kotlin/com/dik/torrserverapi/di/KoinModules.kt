package com.dik.torrserverapi.di

import com.dik.common.AppDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.core.Koin
import org.koin.dsl.koinApplication
import org.koin.dsl.module

object KoinModules {
    val koin: Koin by lazy {
        koinApplication {
            modules(torrserverModule, httpModule)
        }.koin
    }

    fun init(appDispatchers: AppDispatchers) {
        koin.loadModules(
            listOf(
                module {
                    single<AppDispatchers> { appDispatchers }
                    single<CoroutineScope> {
                        CoroutineScope(SupervisorJob() + appDispatchers.mainDispatcher())
                    }
                }
            )
        )
    }
}

internal inline fun <reified T> inject(): T {
    return KoinModules.koin.get()
}