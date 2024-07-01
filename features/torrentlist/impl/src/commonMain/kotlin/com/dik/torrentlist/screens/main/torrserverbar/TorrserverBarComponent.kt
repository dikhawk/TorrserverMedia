package com.dik.torrentlist.screens.main.torrserverbar

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.StateFlow

interface TorrserverBarComponent {

    val uiState: StateFlow<TorrserverBarState>

    fun onClickInstallServer()

    fun onClickRestartServer()
}


@Stable
data class TorrserverBarState(val serverStatus: String = "")