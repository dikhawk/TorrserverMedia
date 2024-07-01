package com.dik.torrentlist.screens.main.torrserverbar

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.dik.common.AppDispatchers
import com.dik.common.Result
import com.dik.torrentlist.di.inject
import com.dik.torrserverapi.server.TorrserverCommands
import com.dik.torrserverapi.server.TorrserverStuffApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

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
        serverStatus()
        lifecycle.doOnDestroy {
            componentScope.cancel()
        }
    }

    override fun onClickInstallServer() {
        componentScope.launch(dispatcher.defaultDispatcher()) {
            torrserverCommands.installServer().collect { restult ->
                when (val res = restult) {
                    is Result.Loading -> _uiState.update {
                        it.copy(isShowProgress = true, progressUpdate = res.progress.progress)
                    }

                    is Result.Error -> _uiState.update {
                        it.copy(isShowProgress = false, error = res.error.toString())
                    }

                    is Result.Success -> _uiState.update {
                        it.copy(isShowProgress = false, serverStatus = "!!!!!INSTALLED!!!!")
                    }
                }
            }
        }
    }

    override fun onClickRestartServer() {
        componentScope.launch(dispatcher.defaultDispatcher()) {
            torrserverCommands.startServer()
        }
    }

    override fun onStopServer() {
       componentScope.launch(dispatcher.defaultDispatcher()) {
           torrserverCommands.stopServer()
       }
    }

    private fun serverStatus() {
        componentScope.launch(dispatcher.defaultDispatcher()) {
            while (true) {
                val result = torrserverStuffApi.echo()
                when(val res = result) {
                    is Result.Error -> _uiState.update { it.copy(serverStatus = res.error.toString()) }
                    is Result.Loading -> TODO()
                    is Result.Success -> _uiState.update { it.copy(serverStatus = res.data) }
                }
                delay(1000)
            }
        }
    }
}