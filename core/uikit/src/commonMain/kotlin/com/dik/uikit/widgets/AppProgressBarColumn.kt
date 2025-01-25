package com.dik.uikit.widgets

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun AppProgressBarColumn(
    modifier: Modifier = Modifier,
    progressBarText: String? = null,
    isShowProgressBar: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(modifier = modifier) {
        Column(modifier = Modifier.fillMaxSize()) { content() }
        if (isShowProgressBar) {
            AppProgressBar(modifier = Modifier.align(Alignment.Center), text = progressBarText)
        }
    }
}