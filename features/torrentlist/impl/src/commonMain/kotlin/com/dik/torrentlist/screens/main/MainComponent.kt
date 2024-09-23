package com.dik.torrentlist.screens.main

import androidx.compose.runtime.Stable
import com.dik.torrentlist.screens.components.bufferization.BufferizationComponent
import com.dik.torrentlist.screens.details.DetailsComponent
import com.dik.torrentlist.screens.main.appbar.MainAppBarComponent
import com.dik.torrentlist.screens.main.list.TorrentListComponent
import com.dik.torrentlist.screens.main.torrserverbar.TorrserverBarComponent
import kotlinx.coroutines.flow.StateFlow

internal interface MainComponent {

    val uiState: StateFlow<MainComponentState>

    val mainAppBarComponent: MainAppBarComponent

    val torrserverBarComponent: TorrserverBarComponent

    val torrentListComponent: TorrentListComponent

    val detailsComponent: DetailsComponent

    val bufferizationComponent: BufferizationComponent
}

@Stable
internal data class MainComponentState(
    val isShowDetails: Boolean = false,
    val isServerStarted: Boolean = false,
    val isShowBufferization: Boolean = false,
)