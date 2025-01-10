package com.dik.torrentlist

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.dik.common.navigation.FeatureEntry

abstract class TorrentListEntry : FeatureEntry {
    abstract fun root(
        pathToTorrent: String? = null,
        context: ComponentContext,
        onFinish: () -> Unit = {}
    ): @Composable () -> Unit
}