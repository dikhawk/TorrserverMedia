package com.dik.uikit.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun currentWindowSizeHeight(): Dp = LocalWindowInfo.current.containerSize.height.toDp()

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun currentWindowSizeWidth(): Dp = LocalWindowInfo.current.containerSize.width.toDp()

@Composable
private fun Int.toDp(): Dp = with(LocalDensity.current) { this@toDp.toDp() }