package com.dik.torrentlist

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.arkivanov.decompose.ComponentContext
import com.dik.torrentlist.screens.navigation.DefaultRootComponent
import com.dik.torrentlist.screens.navigation.RootUi

class TorrentListEntryImpl() : TorrentListEntry() {

    override fun root(
        pathToTorrent: String?,
        context: ComponentContext,
        onFinish: () -> Unit
    ): @Composable () -> Unit = {
        val rootComponent = DefaultRootComponent(componentContext = context)

        RootUi(rootComponent)

    }

    override fun root(context: ComponentContext, onFinish: () -> Unit): @Composable () -> Unit = {
        RootUi(DefaultRootComponent(componentContext = context))
    }
}