package com.dik.uikit.widgets

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.vectorResource
import torrservermedia.core.uikit.generated.resources.Res
import torrservermedia.core.uikit.generated.resources.ic_arrow_back_24

@Composable
fun AppIconButtonArrowBack(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick, modifier = modifier) {
        Icon(
            imageVector = vectorResource(Res.drawable.ic_arrow_back_24),
            contentDescription = null,
        )
    }
}