package com.dik.torrentlist.screens.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dik.torrentlist.screens.components.bufferization.BufferizationUi
import com.dik.torrentlist.screens.details.DetailsUi
import com.dik.torrentlist.screens.main.appbar.MainAppBarUi
import com.dik.torrentlist.screens.main.list.TorrentListUi
import com.dik.torrentlist.screens.main.torrserverbar.TorrserverBarUi
import com.dik.uikit.widgets.AppNormalText
import org.jetbrains.compose.resources.stringResource
import torrservermedia.features.torrentlist.impl.generated.resources.Res
import torrservermedia.features.torrentlist.impl.generated.resources.main_stub_server_not_started

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
                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    val minRightColumnWidth = 400.dp
                    val maxRightColumnWidth = 500.dp

                    val screenWidth = maxWidth
                    val calculatedWidth = screenWidth / 3

                    val rightColumnWidth = when {
                        calculatedWidth < minRightColumnWidth -> minRightColumnWidth
                        calculatedWidth > maxRightColumnWidth -> maxRightColumnWidth
                        else -> calculatedWidth
                    }

                    Row(modifier = Modifier.fillMaxSize()) {
                        Column(modifier = Modifier.weight(1f).fillMaxHeight().padding(4.dp)) {
                            TorrentListUi(component.torrentListComponent)
                        }

                        if (uiState.value.isShowDetails) {
                            Column(modifier = Modifier.width(rightColumnWidth).fillMaxHeight()) {
                                DetailsUi(
                                    component = component.detailsComponent,
                                    modifier = Modifier.padding(
                                        top = 8.dp,
                                        bottom = 8.dp,
                                        end = 8.dp
                                    )
                                )
                            }
                        }
                    }
                }
            } else {
                NotStartedTorrServerStub()
            }
        }
    }

    if (uiState.value.isShowBufferization) BufferizationUi(component = component.bufferizationComponent)
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