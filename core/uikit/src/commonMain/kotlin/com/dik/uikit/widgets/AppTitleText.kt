package com.dik.uikit.widgets

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dik.uikit.theme.AppTheme

@Composable
fun AppTitleText(text: String, modifier: Modifier = Modifier) {
    AppNormalText(text = text, modifier = modifier)
}

@Preview
@Composable
fun AppTitleTextPreview() {
    AppTheme {
        AppTitleText("Title")
    }
}