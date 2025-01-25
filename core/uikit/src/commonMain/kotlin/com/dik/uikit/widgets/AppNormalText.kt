package com.dik.uikit.widgets

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import com.dik.uikit.theme.AppTheme

@Composable
fun AppNormalText(
    text: String,
    modifier: Modifier = Modifier,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    overflow: TextOverflow = TextOverflow.Clip,
    style: TextStyle? = null
) {
    Text(
        modifier = modifier,
        text = text,
        maxLines = maxLines,
        minLines = minLines,
        overflow = overflow,
        style = if (style != null) style else AppTheme.typography.normalText,
    )
}