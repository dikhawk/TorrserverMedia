package com.dik.torrentlist.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dik.torrentlist.screens.details.DetailsUi
import com.dik.torrentlist.screens.main.list.TorrentListUi
import com.dik.torrentlist.screens.main.torrserverbar.TorrserverBarUi

@Composable
internal fun MainUi(component: MainComponent, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize()) {
        Text("MainUi")
        TorrserverBarUi(component.torrserverBarComponent())

        Row(modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                TorrentListUi(component.torrentListComponent())
            }
            Column(modifier = Modifier.width(400.dp)) {
                DetailsUi(component.detailsComponent)
            }
        }
    }
}