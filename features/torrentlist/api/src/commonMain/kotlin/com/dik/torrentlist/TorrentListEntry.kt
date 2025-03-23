package com.dik.torrentlist

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.dik.common.navigation.FeatureEntry

abstract class TorrentListEntry : FeatureEntry {

    abstract fun openTorrent(
        pathToTorrent: String,
        context: ComponentContext,
        onFinish: () -> Unit = {}
    ): @Composable () -> Unit

    abstract fun openMagnet(
        magnetLink: String,
        context: ComponentContext,
        onFinish: () -> Unit = {}
    ): @Composable () -> Unit
}