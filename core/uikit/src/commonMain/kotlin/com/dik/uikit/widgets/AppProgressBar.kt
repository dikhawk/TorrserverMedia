package com.dik.uikit.widgets

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AppProgressBar(modifier: Modifier = Modifier, text: String? = null) {
    Card(modifier = modifier.defaultMinSize(minHeight = 150.dp).padding(16.dp)) {
        if (text!= null) {
            AppNormalText(text)
        }
        Spacer(modifier = modifier.padding(8.dp))
        LinearProgressIndicator()
    }
}