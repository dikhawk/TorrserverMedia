package com.dik.torrentlist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.arkivanov.decompose.ComponentContext
import com.dik.torrentlist.screens.navigation.DefaultRootComponent
import com.dik.torrentlist.screens.navigation.RootUi

class TorrentListEntryImpl() : TorrentListEntry() {

    override fun composableMain(componentContex: ComponentContext): @Composable () -> Unit = {
        RootUi(DefaultRootComponent(componentContex))
    }
}