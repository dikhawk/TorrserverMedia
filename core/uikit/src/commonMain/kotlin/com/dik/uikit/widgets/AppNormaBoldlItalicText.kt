package com.dik.uikit.widgets

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.dik.uikit.theme.AppTheme
import com.dik.uikit.theme.AppTypography
import com.dik.uikit.theme.LocalAppTypography

@Composable
fun AppNormaBoldlItalicText(
    text: String,
    modifier: Modifier = Modifier,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    overflow: TextOverflow = TextOverflow.Clip,
) {
    AppNormalText(
        text = text,
        modifier = modifier,
        maxLines = maxLines,
        minLines = minLines,
        overflow = overflow,
        style = AppTheme.typography.normalBoldItalicText
    )
}