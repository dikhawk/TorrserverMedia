package com.dik.torrservermedia.di

import com.dik.common.platform.PlatformEvensManager
import com.dik.common.platform.PlatformEvents
import com.dik.common.platform.intent.PlatformIntent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.koin.dsl.module

internal fun platformEventsModule() = module {
    single<PlatformEvensManager> { platformEventsManager() }
    single<PlatformEvents> { platformEvents() }
}

private fun platformEventsManager() = object : PlatformEvensManager {

    private val systemEventsFlow = MutableSharedFlow<PlatformIntent>()

    override fun systemEventsFlow(): MutableSharedFlow<PlatformIntent> = systemEventsFlow
}

private fun platformEvents() = object : PlatformEvents {
    private val platformEvensManager: PlatformEvensManager = inject()

    override fun systemEventsFlow(): SharedFlow<PlatformIntent> =
        platformEvensManager.systemEventsFlow().asSharedFlow()
}