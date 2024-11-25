package com.dik.uikit.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun currentWindowSize(): WindowSizeClass {
    val height: Dp = currentWindowSizeHeight()
    val width: Dp = currentWindowSizeWidth()

    return WindowSizeClass(
        windowHeightSizeClass = height.toWindowSizeHeight(),
        windowWidthSizeClass = width.toWindowSizeWidth(),
        windowHeightDp = height,
        windowWidthDp = width,
    )
}

@Composable
expect fun currentWindowSizeHeight(): Dp

@Composable
expect fun currentWindowSizeWidth(): Dp

data class WindowSizeClass(
    val windowHeightSizeClass: WindowSize.Height,
    val windowWidthSizeClass: WindowSize.Width,
    val windowHeightDp: Dp,
    val windowWidthDp: Dp
)

fun Dp.toWindowSizeWidth(): WindowSize.Width {
    return when {
        this < 600.dp -> WindowSize.Width.COMPACT
        600.dp <= this && this <= 840.dp -> WindowSize.Width.MEDIUM
        else -> WindowSize.Width.LARGE
    }
}

fun Dp.toWindowSizeHeight(): WindowSize.Height {
    return when {
        this < 480.dp -> WindowSize.Height.COMPACT
        480.dp <= this && this <= 900.dp -> WindowSize.Height.MEDIUM
        else -> WindowSize.Height.LARGE
    }
}

sealed interface WindowSize {
    enum class Width : WindowSize {
        COMPACT,
        MEDIUM,
        LARGE
    }

    enum class Height : WindowSize {
        COMPACT,
        MEDIUM,
        LARGE
    }
}