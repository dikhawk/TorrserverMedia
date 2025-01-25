package com.dik.torrentlist.screens.details

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dik.uikit.widgets.AppNormalBoldText
import com.dik.uikit.widgets.AppNormalItalicText
import com.dik.uikit.widgets.AppNormalVerticalSpacer

@Composable
internal fun AboutContentUi(
    title: String,
    overview: String,
    season: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        AnimatedVisibility(title.isNotEmpty()) {
            AppNormalVerticalSpacer()
            AppNormalBoldText(text = title)
        }
        AnimatedVisibility(season.isNotEmpty()) {
            AppNormalVerticalSpacer()
            AppNormalItalicText(text = season)
        }
        AnimatedVisibility(overview.isNotEmpty()) {
            AppNormalVerticalSpacer()
            AppNormalItalicText(text = overview)
        }
    }
}