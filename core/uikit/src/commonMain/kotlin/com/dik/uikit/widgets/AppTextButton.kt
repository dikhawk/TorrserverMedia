package com.dik.uikit.widgets

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.dik.uikit.theme.AppTheme

@Composable
fun AppTextButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    textColorEnabled: Color = MaterialTheme.colorScheme.primary,
    textColorDisabled: Color = MaterialTheme.colorScheme.inversePrimary,
    onClick: () -> Unit
) {
    TextButton(modifier = modifier, enabled = enabled, onClick = onClick) {
        Text(
            text = text, style = AppTheme.typography.normalText,
            color = if (enabled) textColorEnabled else textColorDisabled
        )
    }
}