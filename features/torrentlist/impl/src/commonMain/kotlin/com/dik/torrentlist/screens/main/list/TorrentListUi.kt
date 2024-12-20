package com.dik.torrentlist.screens.main.list

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
internal expect fun SharedTransitionScope.TorrentListUi(
    component: TorrentListComponent,
    modifier: Modifier = Modifier,
    isVisible: Boolean
)