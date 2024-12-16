package com.dik.uikit.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

private val lightScheme = lightColorScheme(
    primary = lightColors.primary,
    onPrimary = lightColors.onPrimary,
    primaryContainer = lightColors.primaryContainer,
    onPrimaryContainer = lightColors.onPrimaryContainer,
    secondary = lightColors.secondary,
    onSecondary = lightColors.onSecondary,
    secondaryContainer = lightColors.secondaryContainer,
    onSecondaryContainer = lightColors.onSecondaryContainer,
    tertiary = lightColors.tertiary,
    onTertiary = lightColors.onTertiary,
    tertiaryContainer = lightColors.tertiaryContainer,
    onTertiaryContainer = lightColors.onTertiaryContainer,
    error = lightColors.error,
    onError = lightColors.onError,
    errorContainer = lightColors.errorContainer,
    onErrorContainer = lightColors.onErrorContainer,
    background = lightColors.background,
    onBackground = lightColors.onBackground,
    surface = lightColors.surface,
    onSurface = lightColors.onSurface,
    surfaceVariant = lightColors.surfaceVariant,
    onSurfaceVariant = lightColors.onSurfaceVariant,
    outline = lightColors.outline,
    outlineVariant = lightColors.outlineVariant,
    scrim = lightColors.scrim,
    inverseSurface = lightColors.inverseSurface,
    inverseOnSurface = lightColors.inverseOnSurface,
    inversePrimary = lightColors.inversePrimary,
    surfaceDim = lightColors.surfaceDim,
    surfaceBright = lightColors.surfaceBright,
    surfaceContainerLowest = lightColors.surfaceContainerLowest,
    surfaceContainerLow = lightColors.surfaceContainerLow,
    surfaceContainer = lightColors.surfaceContainer,
    surfaceContainerHigh = lightColors.surfaceContainerHigh,
    surfaceContainerHighest = lightColors.surfaceContainerHighest,
)

private val darkScheme = darkColorScheme(
    primary = darkColors.primary,
    onPrimary = darkColors.onPrimary,
    primaryContainer = darkColors.primaryContainer,
    onPrimaryContainer = darkColors.onPrimaryContainer,
    secondary = darkColors.secondary,
    onSecondary = darkColors.onSecondary,
    secondaryContainer = darkColors.secondaryContainer,
    onSecondaryContainer = darkColors.onSecondaryContainer,
    tertiary = darkColors.tertiary,
    onTertiary = darkColors.onTertiary,
    tertiaryContainer = darkColors.tertiaryContainer,
    onTertiaryContainer = darkColors.onTertiaryContainer,
    error = darkColors.error,
    onError = darkColors.onError,
    errorContainer = darkColors.errorContainer,
    onErrorContainer = darkColors.onErrorContainer,
    background = darkColors.background,
    onBackground = darkColors.onBackground,
    surface = darkColors.surface,
    onSurface = darkColors.onSurface,
    surfaceVariant = darkColors.surfaceVariant,
    onSurfaceVariant = darkColors.onSurfaceVariant,
    outline = darkColors.outline,
    outlineVariant = darkColors.outlineVariant,
    scrim = darkColors.scrim,
    inverseSurface = darkColors.inverseSurface,
    inverseOnSurface = darkColors.inverseOnSurface,
    inversePrimary = darkColors.inversePrimary,
    surfaceDim = darkColors.surfaceDim,
    surfaceBright = darkColors.surfaceBright,
    surfaceContainerLowest = darkColors.surfaceContainerLowest,
    surfaceContainerLow = darkColors.surfaceContainerLow,
    surfaceContainer = darkColors.surfaceContainer,
    surfaceContainerHigh = darkColors.surfaceContainerHigh,
    surfaceContainerHighest = darkColors.surfaceContainerHighest,
)

object AppTheme {
    val colors: AppColors @Composable get() = LocalAppColors.current
    val typography: AppTypography @Composable get() = LocalAppTypography.current
}

@Composable
fun AppTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (isDarkTheme) darkScheme else lightScheme,
        content = {
            CompositionLocalProvider(
                LocalAppTypography provides if (isDarkTheme) AppTypographyDark() else AppTypographyLight(),
                LocalAppColors provides if (isDarkTheme) darkColors else lightColors,

                content = content
            )
        }
    )
}

