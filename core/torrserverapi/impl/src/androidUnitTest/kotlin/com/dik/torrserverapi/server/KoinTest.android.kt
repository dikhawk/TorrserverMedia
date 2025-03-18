package com.dik.torrserverapi.server

import android.content.Context
import io.mockk.mockk
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformSpecificModule(): Module = module {
    single<Context> { mockk(relaxed = true) }
}