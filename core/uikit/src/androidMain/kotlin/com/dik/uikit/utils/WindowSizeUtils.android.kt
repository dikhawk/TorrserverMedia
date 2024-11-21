package com.dik.uikit.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
actual fun currentWindowSizeHeight(): Dp = LocalConfiguration.current.screenHeightDp.dp

@Composable
actual fun currentWindowSizeWidth(): Dp = LocalConfiguration.current.screenWidthDp.dp