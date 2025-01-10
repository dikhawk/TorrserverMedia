package com.dik.common.platform

import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import kotlinx.coroutines.flow.MutableStateFlow

interface WindowAdaptiveObserver {

    fun windowAdaptiveFlow(): MutableStateFlow<WindowAdaptiveInfo?>
}