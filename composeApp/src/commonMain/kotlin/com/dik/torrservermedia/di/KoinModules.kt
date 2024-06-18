package com.dik.torrservermedia.di

import com.arkivanov.decompose.ComponentContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

object KoinModules {
    fun init() {

        startKoin {
            modules(
                appModule,
                featuresModule,
                torrserverModule,
            )
        }
    }
}