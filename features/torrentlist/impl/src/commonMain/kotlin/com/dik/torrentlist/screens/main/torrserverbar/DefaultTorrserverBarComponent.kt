package com.dik.torrentlist.screens.main.torrserverbar

import com.arkivanov.decompose.ComponentContext
import com.dik.common.AppDispatchers
import com.dik.common.Result
import com.dik.common.ResultProgress
import com.dik.common.utils.successResult
import com.dik.torrentlist.error.toMessage
import com.dik.torrserverapi.TorrserverError
import com.dik.torrserverapi.server.TorrserverCommands
import com.dik.torrserverapi.server.TorrserverStuffApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import torrservermedia.features.torrentlist.impl.generated.resources.Res
import torrservermedia.features.torrentlist.impl.generated.resources.main_torrserver_bar_msg_installing_torrserver
import torrservermedia.features.torrentlist.impl.generated.resources.main_torrserver_bar_msg_server_not_installed

internal class DefaultTorrserverBarComponent(
    context: ComponentContext,
    private val torrserverCommands: TorrserverCommands,
    private val dispatchers: AppDispatchers,
    private val componentScope: CoroutineScope,
) : TorrserverBarComponent, ComponentContext by context {

    private val _uiState = MutableStateFlow(TorrserverBarState())
    override val uiState: StateFlow<TorrserverBarState> = _uiState.asStateFlow()


    override fun onClickInstallServer() {
        _uiState.update { it.copy(isShowProgress = true) }
        componentScope.launch(dispatchers.defaultDispatcher()) {
            torrserverCommands.installServer().collect { restult ->
                when (val res = restult) {
                    is ResultProgress.Loading -> _uiState.update {
                        it.copy(
                            isShowProgress = true,
                            progressValue = res.progress.progress.toFloat(),
                            serverStatusText = getString(Res.string.main_torrserver_bar_msg_installing_torrserver),
                        )
                    }

                    is ResultProgress.Error -> _uiState.update {
                        it.copy(isShowProgress = false, error = res.error.toMessage())
                    }

                    is ResultProgress.Success -> {
                        _uiState.update {
                            it.copy(
                                isShowProgress = false,
                                isServerInstalled = true
                            )
                        }
//                        torrserverCommands.startServer()
                        startTorserver()
                    }
                }
            }
        }
    }

    override fun onClickStartServer() {
        componentScope.launch(dispatchers.defaultDispatcher()) {
            startTorserver()
/*            val result = torrserverCommands.startServer()
            _uiState.update { it.copy(isServerStarted = result is Result.Success) }
            checkServerIsInstalled()*/
        }
    }

/*    private fun checkServerIsInstalled() {
        componentScope.launch {
            val isInstalled = isServerInstalled()
            val isStarted = isServerStarted()
            _uiState.update {
                it.copy(
                    serverStatusText = getString(Res.string.main_torrserver_bar_msg_server_not_installed),
                    isServerInstalled = isInstalled,
                    isServerStarted = isStarted
                )
            }
        }
    }*/

/*    private suspend fun isServerInstalled(): Boolean {
        val result = torrserverCommands.isServerInstalled()

        return result.successResult(::showError) ?: false
    }

    private suspend fun isServerStarted(): Boolean {
        val result = torrserverCommands.isServerStarted()

        return result.successResult(::showError) ?: false
    }*/

    private fun showError(error: TorrserverError) {
        _uiState.update { it.copy(serverStatusText = error.toString()) }
    }
}

internal expect suspend fun startTorserver()