package com.dik.uikit.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp

@Composable
expect fun currentWindowSizeHeight(): Dp

@Composable
expect fun currentWindowSizeWidth(): Dp