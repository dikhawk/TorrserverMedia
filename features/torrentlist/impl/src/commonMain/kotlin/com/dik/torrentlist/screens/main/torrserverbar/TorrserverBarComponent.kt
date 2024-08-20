package com.dik.torrentlist.screens.main.torrserverbar

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.StateFlow

internal interface TorrserverBarComponent {

    val uiState: StateFlow<TorrserverBarState>

    fun onClickInstallServer()

    fun onClickStartServer()

    fun onClickStopServer()
}


@Stable
internal data class TorrserverBarState(
    val serverStatusText: String = "Not initialized",
    val isShowProgress: Boolean = false,
    val progressUpdate: Double = 0.0,
    val error: String? = null,
    val isServerStarted: Boolean = true,
    val isServerInstalled: Boolean = true,
)