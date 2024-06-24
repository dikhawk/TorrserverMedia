package com.dik.torrentlist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.arkivanov.decompose.ComponentContext
import com.dik.torrserverapi.server.TorrserverStuffApi
import org.koin.mp.KoinPlatform

class TorrentListEntryImpl() : TorrentListEntry() {

    override fun composableMain(componentContex: ComponentContext): @Composable () -> Unit = {
        val scope = rememberCoroutineScope()

        MainTorrentListUi(DefaultMainTorrentListComponent(
            componentContext = componentContex,
            coroutineScope = scope
        ))
    }
}