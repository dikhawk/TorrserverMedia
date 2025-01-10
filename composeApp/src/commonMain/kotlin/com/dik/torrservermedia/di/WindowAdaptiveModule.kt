package com.dik.torrservermedia.di

import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import com.dik.common.platform.WindowAdaptiveClient
import com.dik.common.platform.WindowAdaptiveObserver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.dsl.module

internal val windowAdaptiveModule = module {
    single<WindowAdaptiveObserver> { windowAdaptiveObserver() }
    single<WindowAdaptiveClient> {
        val observer: WindowAdaptiveObserver = get()

        object : WindowAdaptiveClient {
            override fun windowAdaptiveFlow(): StateFlow<WindowAdaptiveInfo?> {
                return observer.windowAdaptiveFlow().asStateFlow()
            }
        }
    }
}

private fun windowAdaptiveObserver() = object : WindowAdaptiveObserver {
    private val windowAdaptiveFlow = MutableStateFlow<WindowAdaptiveInfo?>(null)

    override fun windowAdaptiveFlow(): MutableStateFlow<WindowAdaptiveInfo?> {
        return windowAdaptiveFlow
    }
}