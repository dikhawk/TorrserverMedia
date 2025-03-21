package com.dik.uikit.widgets

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import com.dik.uikit.theme.AppTheme

@Composable
fun AppNormalBoldText(
    text: String,
    modifier: Modifier = Modifier,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    overflow: TextOverflow = TextOverflow.Clip,
    color: Color = Color.Unspecified
) {
    AppNormalText(
        text = text,
        modifier = modifier,
        maxLines = maxLines,
        minLines = minLines,
        overflow = overflow,
        style = AppTheme.typography.normalBoldText.copy(
            color = if (color == Color.Unspecified) AppTheme.typography.normalBoldText.color else color
        ),
    )
}