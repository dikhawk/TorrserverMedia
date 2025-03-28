package com.dik.torrentlist.screens.main

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowWidthSizeClass
import com.dik.torrentlist.screens.components.bufferization.BufferizationUi
import com.dik.torrentlist.screens.details.DetailsPaneUi
import com.dik.torrentlist.screens.main.appbar.MainAppBarUi
import com.dik.torrentlist.screens.main.list.TorrentListUi
import com.dik.torrentlist.screens.main.torrserverbar.TorrserverBarUi
import com.dik.torrserverapi.model.TorrserverStatus
import com.dik.uikit.utils.currentWindowSizeWidth

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
internal fun SharedTransitionScope.MainAdaptiveUi(
    component: MainComponent,
    isVisible: Boolean,
    modifier: Modifier = Modifier,
) {
    val windowSize = currentWindowAdaptiveInfo()
    val uiState by component.uiState.collectAsState()

    Scaffold(
        topBar = { MainAppBarUi(component = component.mainAppBarComponent) }
    ) { paddings ->
        Box(modifier = modifier.padding(paddings).fillMaxSize()) {
            when {
                uiState.serverStatus != TorrserverStatus.STARTED -> {
                    TorrserverBarUi(
                        component = component.torrserverBarComponent,
                        torrserverStatus = uiState.serverStatus,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                windowSize.windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT -> {
                    TorrentListUi(
                        modifier = Modifier.padding(4.dp),
                        component = component.torrentListComponent,
                        isVisible = isVisible
                    )
                }

                else -> {
                    MainTwoPaneUi(component = component, isVisible = isVisible)
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

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
internal fun SharedTransitionScope.MainTwoPaneUi(
    component: MainComponent,
    isVisible: Boolean,
    modifier: Modifier = Modifier
) {
    val uiState = component.uiState.collectAsState()
    val windowWidthDp = currentWindowSizeWidth()

    Column(modifier = modifier) {
        val minRightColumnWidth = 400.dp
        val maxRightColumnWidth = 500.dp

        val calculatedWidth = windowWidthDp / 3

        val rightColumnWidth = when {
            calculatedWidth < minRightColumnWidth -> minRightColumnWidth
            calculatedWidth > maxRightColumnWidth -> maxRightColumnWidth
            else -> calculatedWidth
        }

        Row(modifier = Modifier.fillMaxSize()) {
            TorrentListUi(
                modifier = Modifier.weight(1f).fillMaxHeight().padding(6.dp),
                component = component.torrentListComponent,
                isVisible = isVisible
            )

            if (uiState.value.isShowDetails) {
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