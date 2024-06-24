package com.dik.torrentlist.screens.main.torrserverbar

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.StateFlow

class DefaultTorrserverBarComponent(
    context: ComponentContext,
    ) : TorrserverBarComponent, ComponentContext by context {
    override val uiState: StateFlow<TorrserverBar>
        get() = TODO("Not yet implemented")

    override fun onClickInstall() {
        TODO("Not yet implemented")
    }

    override fun onClickRestartServer() {
        TODO("Not yet implemented")
    }
}