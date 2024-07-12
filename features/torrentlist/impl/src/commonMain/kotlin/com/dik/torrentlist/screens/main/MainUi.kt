package com.dik.torrentlist.screens.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dik.torrentlist.screens.details.DetailsUi
import com.dik.torrentlist.screens.main.appbar.MainAppBarUi
import com.dik.torrentlist.screens.main.list.TorrentListUi
import com.dik.torrentlist.screens.main.torrserverbar.TorrserverBarUi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MainUi(component: MainComponent, modifier: Modifier = Modifier) {
    Scaffold(
        topBar = { MainAppBarUi(component.mainAppBarComponent) }
    ) {
        Column(modifier = modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(64.dp))

            TorrserverBarUi(component.torrserverBarComponent)

            Row(modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f).sizeIn(minWidth = 400.dp).padding(4.dp)) {
                    TorrentListUi(component.torrentListComponent)
                }
                Column(modifier = Modifier.sizeIn(maxWidth = 400.dp)) {
                    DetailsUi(
                        component = component.detailsComponent,
                        modifier = Modifier.padding(top = 8.dp, bottom = 8.dp, end = 8.dp)
                    )
                }
            }
        }
    }
}