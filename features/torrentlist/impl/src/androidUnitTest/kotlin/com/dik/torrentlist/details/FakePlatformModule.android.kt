package com.dik.torrentlist.details

import android.content.Context
import io.mockk.mockk
import org.koin.dsl.module

actual fun platformModule() = module {
    single<Context> { mockk(relaxed = true) }
}