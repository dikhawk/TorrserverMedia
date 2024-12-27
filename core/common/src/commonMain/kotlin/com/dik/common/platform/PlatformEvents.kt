package com.dik.common.platform

import com.dik.common.platform.intent.PlatformIntent
import kotlinx.coroutines.flow.SharedFlow

interface PlatformEvents {

    fun systemEventsFlow(): SharedFlow<PlatformIntent>
}