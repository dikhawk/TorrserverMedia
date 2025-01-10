package com.dik.common.platform

import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import kotlinx.coroutines.flow.StateFlow

interface WindowAdaptiveClient {

    fun windowAdaptiveFlow(): StateFlow<WindowAdaptiveInfo?>
}