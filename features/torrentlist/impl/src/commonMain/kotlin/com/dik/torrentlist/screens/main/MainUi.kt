package com.dik.torrentlist.screens.main

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dik.common.utils.platformName
import com.dik.torrentlist.domain.ServerStatus
import com.dik.torrentlist.screens.components.bufferization.BufferizationUi
import com.dik.torrentlist.screens.details.DetailsPaneUi
import com.dik.torrentlist.screens.main.appbar.MainAppBarUi
import com.dik.torrentlist.screens.main.list.TorrentListUi
import com.dik.torrentlist.screens.main.torrserverbar.TorrserverBarUi
import com.dik.uikit.utils.currentWindowSizeWidth

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
internal fun MainAdaptiveUi(
    component: MainComponent,
    modifier: Modifier = Modifier,
) {
    val uiState by component.uiState.collectAsState()

    Scaffold(
        topBar = { MainAppBarUi(component = component.mainAppBarComponent) }
    ) { paddings ->
        BoxWithConstraints(modifier.padding(paddings).fillMaxSize()) {
            val isMobile = maxWidth < 600.dp

            when(uiState.serverStatus) {
                ServerStatus.General.Started -> {
                    if (isMobile) {
                        TorrentListUi(
                            modifier = Modifier.padding(4.dp),
                            component = component.torrentListComponent,
                            isMultiPane = false
                        )
                    } else {
                        MainTwoPaneUi(component = component)
                    }
                }
                else -> {
                    TorrserverBarUi(
                        component = component.torrserverBarComponent,
                        serverStatus = uiState.serverStatus,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }

    if (uiState.isShowBufferization) {
        BufferizationUi(component = component.bufferizationComponent)
    }

    CloseApp()
}

@Composable
internal expect fun CloseApp()

@Composable
internal fun MainTwoPaneUi(
    component: MainComponent,
    modifier: Modifier = Modifier,
    minRightColumnWidth: Dp = 400.dp,
    maxRightColumnWidth: Dp = 500.dp
) {
    val uiState by component.uiState.collectAsState()
    val windowWidthDp = currentWindowSizeWidth()
    remember { platformName() }

    Column(modifier = modifier) {
        val rightColumnWidth = remember(windowWidthDp) {
            val calculatedWidth = windowWidthDp / 3
            calculatedWidth.coerceIn(minRightColumnWidth, maxRightColumnWidth)
        }

        Row(modifier = Modifier.fillMaxSize()) {
            TorrentListUi(
                modifier = Modifier.weight(1f)
                    .fillMaxHeight()
                    .padding(6.dp),
                component = component.torrentListComponent,
                isMultiPane = true
            )

            if (uiState.isShowDetails) {
                Column(
                    modifier = Modifier.width(rightColumnWidth).fillMaxHeight()
                ) {
                    DetailsPaneUi(
                        component = component.detailsComponent,
                        modifier = Modifier.padding(top = 8.dp, bottom = 8.dp, end = 8.dp)
                    )
                }
            }
        }
    }
}