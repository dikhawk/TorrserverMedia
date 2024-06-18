package com.dik.torrservermedia.di

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.dik.common.AppDispatchers
import com.dik.torrservermedia.nanigation.DefaultRootComponent
import com.dik.torrservermedia.nanigation.RootComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module

val appModule = module {
    single<AppDispatchers> {
        object : AppDispatchers {
            override fun ioDispatcher(): CoroutineDispatcher {
                TODO("Not yet implemented")
            }

            override fun defaultDispatcher() = Dispatchers.Default
            override fun mainDispatcher() = Dispatchers.Main

        }
    }
}