package com.dik.torrservermedia.di

import android.content.Context
import org.koin.dsl.module

internal actual fun platformModule() = module {
    single<ContextProvider> { ContextProvider(get()) }
}

data class ContextProvider(val context: Context)