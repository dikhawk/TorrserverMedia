package com.dik.common.navigation

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext

interface FeatureEntry {
    fun root(
        context: ComponentContext,
        onFinish: () -> Unit = {}
    ): @Composable () -> Unit
}