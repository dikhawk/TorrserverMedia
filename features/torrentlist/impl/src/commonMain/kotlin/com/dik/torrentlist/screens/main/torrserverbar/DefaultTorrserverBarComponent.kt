package com.dik.torrentlist.screens.main.torrserverbar

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.dik.common.AppDispatchers
import com.dik.common.Result
import com.dik.common.ResultProgress
import com.dik.common.utils.successResult
import com.dik.torrentlist.error.toMessage
import com.dik.torrserverapi.TorrserverError
import com.dik.torrserverapi.server.TorrserverCommands
import com.dik.torrserverapi.server.TorrserverStuffApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.getString
import torrservermedia.features.torrentlist.impl.generated.resources.Res
import torrservermedia.features.torrentlist.impl.generated.resources.main_torrserver_bar_msg_installing_torrserver

internal class DefaultTorrserverBarComponent(
    context: ComponentContext,
    private val torrserverStuffApi: TorrserverStuffApi,
    private val torrserverCommands: TorrserverCommands,
    private val dispatchers: AppDispatchers,
    private val componentScope: CoroutineScope,
) : TorrserverBarComponent, ComponentContext by context {

    private val _uiState = MutableStateFlow(TorrserverBarState())
    override val uiState: StateFlow<TorrserverBarState> = _uiState.asStateFlow()

    init {
        checkServerIsInstalled()
        observeServerStatus()
    }

    override fun onClickInstallServer() {
        componentScope.launch(dispatchers.defaultDispatcher()) {
            torrserverCommands.installServer().collect { restult ->
                when (val res = restult) {
                    is ResultProgress.Loading -> _uiState.update {
                        it.copy(
                            isShowProgress = true,
                            progressUpdate = res.progress.progress,
                            serverStatusText = getString(Res.string.main_torrserver_bar_msg_installing_torrserver),
                        )
                    }

                    is ResultProgress.Error -> _uiState.update {
                        it.copy(isShowProgress = false, error = res.error.toMessage())
                    }

                    is ResultProgress.Success -> {
                        _uiState.update { it.copy(isShowProgress = false, isServerInstalled = true) }
                        torrserverCommands.startServer()
                    }
                }
            }
        }
    }

    override fun onClickStartServer() {
        componentScope.launch(dispatchers.defaultDispatcher()) {
            val result = torrserverCommands.startServer()
            _uiState.update { it.copy(isServerStarted = result is Result.Success) }
            checkServerIsInstalled()
        }
    }

    override fun onClickStopServer() {
        componentScope.launch(dispatchers.defaultDispatcher()) {
            torrserverCommands.stopServer()
            _uiState.update { it.copy(isServerStarted = false) }
        }
    }

    private fun observeServerStatus() {
        componentScope.launch {
            torrserverStuffApi.observerServerStatus().collect { result ->
                when (val res = result) {
                    is Result.Error -> {
                        _uiState.update {
                            it.copy(
                                serverStatusText = res.error.toMessage(),
                                isServerStarted = false
                            )
                        }
                    }

                    is Result.Success -> {
                        _uiState.update {
                            it.copy(
                                serverStatusText = res.data,
                                isServerStarted = true
                            )
                        }
                    }
                }
            }
        }
    }

    private fun checkServerIsInstalled() {
        componentScope.launch {
            val isIstalled = isServerInstalled()
            val isStarted = !isIstalled
            _uiState.update { it.copy(isServerInstalled = isIstalled,  isServerStarted = isStarted) }
        }
    }

    private suspend fun isServerInstalled(): Boolean {
        val result = torrserverCommands.isServerInstalled()

        return result.successResult(::showError) ?: false
    }

    private fun showError(error: TorrserverError) {
        _uiState.update { it.copy(serverStatusText = error.toString()) }
    }
}