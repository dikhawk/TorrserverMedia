package com.dik.uikit.widgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
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
    contentScale: ContentScale = ContentScale.None,
    errorStub: @Composable () -> Unit = { ImageStub(modifier = modifier, image = Res.drawable.ic_movie) }
) {
    val isError = remember { mutableStateOf(false) }

    when {
        url.isNullOrEmpty() -> errorStub.invoke()
        isError.value -> errorStub.invoke()
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
    Box(modifier = modifier.background(Color.Gray)) {
        Image(
            imageVector = vectorResource(image),
            modifier = Modifier.height(100.dp).width(100.dp).align(alignment = Alignment.Center),
            contentDescription = null
        )
    }
}