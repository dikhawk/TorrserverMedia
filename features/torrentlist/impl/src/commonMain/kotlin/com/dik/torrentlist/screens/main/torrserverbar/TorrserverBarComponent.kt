package com.dik.torrentlist.screens.main.torrserverbar

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.StateFlow

interface TorrserverBarComponent {

    val uiState: StateFlow<TorrserverBar>

    fun onClickInstall()

    fun onClickRestartServer()
}


@Stable
data class TorrserverBar(val serverStatus: String)