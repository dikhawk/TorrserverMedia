package com.dik.torrentlist.screens.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dik.torrentlist.screens.details.DetailsPaneUi
import com.dik.torrentlist.screens.main.list.TorrentListUi
import com.dik.uikit.utils.WindowSize
import com.dik.uikit.utils.currentWindowSize
import com.dik.uikit.widgets.AppTopBar
import org.jetbrains.compose.resources.stringResource
import torrservermedia.features.torrentlist.impl.generated.resources.Res
import torrservermedia.features.torrentlist.impl.generated.resources.main_app_bar_title

@Composable
internal fun MainAdaptiveUi(
    component: MainComponent,
    modifier: Modifier = Modifier,
) {
    val windowSize = currentWindowSize()

    Scaffold(
        topBar = { AppTopBar(title = stringResource(Res.string.main_app_bar_title)) }
    ) { paddings ->
        when {
            windowSize.windowWidthSizeClass == WindowSize.Width.COMPACT -> {
                TorrentListUi(
                    component = component.torrentListComponent,
                    modifier = Modifier.padding(paddings)
                )
            }

            else -> {
                MainTwoPaneUi(component = component, modifier = modifier.padding(paddings))
            }
        }
    }
}

@Composable
internal fun MainTwoPaneUi(component: MainComponent, modifier: Modifier = Modifier) {
    val uiState = component.uiState.collectAsState()
    val windowSize = currentWindowSize()

    Column(modifier = modifier) {
        val minRightColumnWidth = 400.dp
        val maxRightColumnWidth = 500.dp

        val screenWidth = windowSize.windowWidthDp
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