package com.dik.torrservermedia.di

import org.koin.core.Koin
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.koinApplication

object KoinModules {

    private var koinConfig: KoinAppDeclaration? = null

    val koin: Koin by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        koinApplication {
            koinConfig?.invoke(this)
            modules(
                appModule,
                platformModule(),
                appSettingsModule,
                featuresModule,
                torrserverModule,
                theMovieDbApiModule
            )
        }.koin
    }

    fun init(config: KoinAppDeclaration? = null): Koin {
        koinConfig = config

        startKoin { }

        return koin
    }
}

internal inline fun <reified T> inject(): T {
    return KoinModules.koin.get()
}
