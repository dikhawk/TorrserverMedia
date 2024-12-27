package com.dik.common.platform

import com.dik.common.platform.intent.PlatformIntent
import kotlinx.coroutines.flow.MutableSharedFlow

interface PlatformEvensManager {

    fun systemEventsFlow(): MutableSharedFlow<PlatformIntent>
}