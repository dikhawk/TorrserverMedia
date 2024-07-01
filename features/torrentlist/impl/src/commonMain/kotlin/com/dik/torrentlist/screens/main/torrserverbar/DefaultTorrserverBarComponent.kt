package com.dik.torrentlist.screens.main.torrserverbar

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.dik.common.AppDispatchers
import com.dik.torrentlist.di.inject
import com.dik.torrserverapi.server.TorrserverCommands
import com.dik.torrserverapi.server.TorrserverStuffApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DefaultTorrserverBarComponent(
    context: ComponentContext,
    private val torrserverStuffApi: TorrserverStuffApi = inject(),
    private val torrserverCommands: TorrserverCommands = inject(),
    private val dispatcher: AppDispatchers = inject()
) : TorrserverBarComponent, ComponentContext by context {

    private val componentScope = CoroutineScope(dispatcher.mainDispatcher() + SupervisorJob())
    private val _uiState = MutableStateFlow(TorrserverBarState())
    override val uiState: StateFlow<TorrserverBarState> = _uiState.asStateFlow()

    init {
        lifecycle.doOnDestroy { componentScope.cancel() }
    }

    override fun onClickInstallServer() {
        componentScope.launch(dispatcher.defaultDispatcher()) {
            torrserverCommands.installServer().collect {

            }
        }
    }

    override fun onClickRestartServer() {
        TODO("Not yet implemented")
    }
}