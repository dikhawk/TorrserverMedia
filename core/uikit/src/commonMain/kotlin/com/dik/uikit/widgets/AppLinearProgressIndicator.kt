package com.dik.uikit.widgets

import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AppLinearProgressIndicator(progress: () -> Float, modifier: Modifier = Modifier) {
    LinearProgressIndicator(modifier = modifier, progress = progress)
}