package com.dik.uikit.widgets

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import torrservermedia.core.uikit.generated.resources.Res
import torrservermedia.core.uikit.generated.resources.ic_add_24

@Composable
fun AppActionButton(
    imageVector: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentDescription: String? = null
) {
    IconButton(onClick = onClick, modifier = modifier, enabled = enabled) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription
        )
    }
}

@Preview
@Composable
private fun AppActionButton() {
    AppActionButton(
        imageVector = vectorResource(Res.drawable.ic_add_24),
        onClick = {},
    )
}