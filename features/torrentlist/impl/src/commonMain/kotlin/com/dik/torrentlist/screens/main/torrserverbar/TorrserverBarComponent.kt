package com.dik.torrentlist.screens.main.torrserverbar

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.StateFlow

interface TorrserverBarComponent {

    val uiState: StateFlow<TorrserverBarState>

    fun onClickInstallServer()

    fun onClickRestartServer()

    fun onStopServer()
}


@Stable
data class TorrserverBarState(
    val serverStatus: String = "Not initialized",
    val isShowProgress: Boolean = false,
    val progressUpdate: Double = 0.0,
    val error: String? = null,
)