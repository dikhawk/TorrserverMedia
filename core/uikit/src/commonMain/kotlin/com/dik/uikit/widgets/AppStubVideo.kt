package com.dik.uikit.widgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.vectorResource
import torrservermedia.core.uikit.generated.resources.Res
import torrservermedia.core.uikit.generated.resources.ic_movie

@Composable
fun AppStubVideo(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize().background(Color.Gray)) {
        Icon(
            imageVector = vectorResource(Res.drawable.ic_movie),
            modifier = Modifier.height(100.dp).width(100.dp).align(alignment = Alignment.Center),
            contentDescription = null
        )
    }
}