package com.dik.torrservermedia.di

import org.koin.core.Koin
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.koinApplication

object KoinModules {

    val koin: Koin by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        koinApplication {
            modules(
                appModule,
                featuresModule,
                torrserverModule,
            )
        }.koin
    }

    fun init(config: KoinAppDeclaration? = null): Koin {
        startKoin {
            config?.invoke(this)
        }

        return koin
    }
}

internal inline fun <reified T> inject(): T {
    return KoinModules.koin.get()
}
