package com.dik.torrentlist.screens.main

import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dik.torrentlist.screens.details.DetailsUi
import com.dik.torrentlist.screens.main.appbar.MainAppBarUi
import com.dik.torrentlist.screens.main.list.TorrentListUi
import com.dik.torrentlist.screens.main.torrserverbar.TorrserverBarUi
import com.dik.uikit.widgets.AppNormalText
import org.jetbrains.compose.resources.stringResource
import torrservermedia.features.torrentlist.impl.generated.resources.Res
import torrservermedia.features.torrentlist.impl.generated.resources.main_stub_server_not_started

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MainUi(component: MainComponent, modifier: Modifier = Modifier) {
    val uiState = component.uiState.collectAsState()

    Scaffold(
        topBar = { MainAppBarUi(component.mainAppBarComponent) }
    ) {
        Column(modifier = modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(64.dp))

            TorrserverBarUi(component.torrserverBarComponent)

            if (uiState.value.isServerStarted) {
                Row(modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f).sizeIn(minWidth = 400.dp).padding(4.dp)) {
                        TorrentListUi(component.torrentListComponent)
                    }
                    if (uiState.value.isShowDetails) {
                        Column(modifier = Modifier.sizeIn(maxWidth = 400.dp)) {
                            DetailsUi(
                                component = component.detailsComponent,
                                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp, end = 8.dp)
                            )
                        }
                    }
                }
            } else {
                NotStartedTorrServerStub()
            }
        }
    }
}

@Composable
private fun NotStartedTorrServerStub(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        AppNormalText(
            text = stringResource(Res.string.main_stub_server_not_started),
            modifier = modifier.align(Alignment.Center)
        )
    }
}