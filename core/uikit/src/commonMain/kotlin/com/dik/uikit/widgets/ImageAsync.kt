package com.dik.uikit.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.vectorResource
import torrservermedia.core.uikit.generated.resources.Res
import torrservermedia.core.uikit.generated.resources.ic_movie

@Composable
fun AppAsyncImage(
    url: String?,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.None
) {
    val isError = remember { mutableStateOf(false) }

    when {
        url.isNullOrEmpty() -> ImageStub(modifier = modifier, image = Res.drawable.ic_movie)
        isError.value -> ImageStub(modifier = modifier, image = Res.drawable.ic_movie)
        else -> AsyncImage(
            modifier = modifier,
            model = url,
            contentDescription = contentDescription,
            contentScale = contentScale,
            onError = { error ->
                isError.value = true
            }
        )
    }
}

@Composable
private fun ImageStub(modifier: Modifier = Modifier, image: DrawableResource) {
    Box(modifier = modifier.fillMaxSize().background(Color.Gray)) {
        Icon(
            imageVector = vectorResource(image),
            modifier = Modifier.height(100.dp).width(100.dp).align(alignment = Alignment.Center),
            contentDescription = null
        )
    }
}