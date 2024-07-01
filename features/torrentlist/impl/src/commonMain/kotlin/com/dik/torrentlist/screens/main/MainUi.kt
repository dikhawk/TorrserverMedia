package com.dik.torrentlist.screens.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dik.torrentlist.screens.main.list.TorrentListUi
import com.dik.torrentlist.screens.main.torrserverbar.TorrserverBarUi

@Composable
fun MainUi(component: MainComponent, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize()) {
        TorrserverBarUi(component.torrserverBarComponent())
        Text("MainUi")
        TorrentListUi(component.torrentListComponent())
    }
}