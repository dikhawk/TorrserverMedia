package com.dik.uikit.widgets

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage

@Composable
fun ImageAsync(
    url: String,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    contentScale: ImageAasyncContentScale
) {
    AsyncImage(
        modifier = modifier,
        model = url,
        contentDescription = contentDescription,
        contentScale = contentScale.coilScale
    )
}

enum class ImageAasyncContentScale(val coilScale: ContentScale) {
    NONE(ContentScale.None),
    CROP(ContentScale.Crop),
    FIT(ContentScale.Fit),
    INSIDE(ContentScale.Inside),
}