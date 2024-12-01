package com.dik.torrentlist.screens.main.torrserverbar

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.StateFlow

internal interface TorrserverBarComponent {

    val uiState: StateFlow<TorrserverBarState>

    fun onClickInstallServer()

    fun onClickStartServer()
}


@Stable
internal data class TorrserverBarState(
    val serverStatusText: String = "Not initialized",
    val isShowProgress: Boolean = false,
    val progressValue: Float = 0.0f,
    val error: String? = null,
    val isServerStarted: Boolean = true,
    val isServerInstalled: Boolean = true,
)