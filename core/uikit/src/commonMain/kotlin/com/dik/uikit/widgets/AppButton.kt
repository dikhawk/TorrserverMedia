package com.dik.uikit.widgets

import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AppButton(text: String, modifier: Modifier = Modifier, enabled : Boolean = true, onClick: () -> Unit) {
    Button(onClick = onClick, enabled = enabled, modifier = modifier) {
        AppNormalText(text = text)
    }
}