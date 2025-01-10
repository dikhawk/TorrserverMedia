package com.dik.torrservermedia.di

import com.dik.common.AppDispatchers
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module

val appModule = module {
    single<AppDispatchers> {
        object : AppDispatchers {
            override fun ioDispatcher() = Dispatchers.Default
            override fun defaultDispatcher() = Dispatchers.Default
            override fun mainDispatcher() = Dispatchers.Main
        }
    }
}