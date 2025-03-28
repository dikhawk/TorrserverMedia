package com.dik.torrentlist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.arkivanov.decompose.ComponentContext
import com.dik.torrentlist.screens.navigation.ChildConfig
import com.dik.torrentlist.screens.navigation.DefaultRootComponent
import com.dik.torrentlist.screens.navigation.RootUi

class TorrentListEntryImpl() : TorrentListEntry() {

    override fun openTorrent(
        pathToTorrent: String,
        context: ComponentContext,
        onFinish: () -> Unit
    ): @Composable () -> Unit = {
        val rootComponent = remember {
            DefaultRootComponent(
                componentContext = context,
                initialConfiguration = ChildConfig.AddTorrent(pathToTorrent)
            )
        }

        RootUi(rootComponent)
    }

    override fun openMagnet(
        magnetLink: String,
        context: ComponentContext,
        onFinish: () -> Unit
    ): @Composable () -> Unit = {
        val rootComponent = remember {
            DefaultRootComponent(
                componentContext = context,
                initialConfiguration = ChildConfig.AddMagnet(magnetLink)
            )
        }

        RootUi(rootComponent)
    }

    override fun root(context: ComponentContext, onFinish: () -> Unit): @Composable () -> Unit = {
        val rootComponent = remember {
            DefaultRootComponent(componentContext = context)
        }

        RootUi(rootComponent)
    }
}