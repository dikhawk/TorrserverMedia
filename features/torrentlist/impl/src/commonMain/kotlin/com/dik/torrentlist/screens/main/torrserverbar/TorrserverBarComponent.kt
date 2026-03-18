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
    val installingState: InstallingState = InstallingState.NotInstalled,
    val reinstallingState: InstallingState = InstallingState.Reinstalling,
    val error: String? = null,
)

internal sealed interface InstallingState {
    data object NotInstalled : InstallingState
    data object Reinstalling : InstallingState
    data object Preparing : InstallingState
    data class Installing(
        val progress: Float,
        val percent: String,
        val currentBytes: String,
        val totalBytes: String
    ) : InstallingState

    data object Installed : InstallingState
    data class Error(val msg: String) : InstallingState
    data class Unknown(val msg: String) : InstallingState
}