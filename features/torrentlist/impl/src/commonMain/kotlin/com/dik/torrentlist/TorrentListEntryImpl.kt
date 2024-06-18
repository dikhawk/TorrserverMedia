package com.dik.torrentlist

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext

class TorrentListEntryImpl() : TorrentListEntry() {

    override fun composableMain(componentContex: ComponentContext): @Composable () -> Unit = {
        MainTorrentListUi(DefaultMainTorrentListComponent(componentContex))
    }
}