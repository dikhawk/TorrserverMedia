package com.dik.torrentlist.screens.main.torrserverbar

import com.arkivanov.decompose.ComponentContext
import com.dik.common.AppDispatchers
import com.dik.common.ResultProgress
import com.dik.common.i18n.LocalizationResource
import com.dik.torrentlist.error.toMessage
import com.dik.torrserverapi.TorrserverError
import com.dik.torrserverapi.server.TorrserverCommands
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import torrservermedia.features.torrentlist.impl.generated.resources.Res
import torrservermedia.features.torrentlist.impl.generated.resources.main_torrserver_bar_msg_installing_torrserver

internal class DefaultTorrserverBarComponent(
    context: ComponentContext,
    private val torrserverCommands: TorrserverCommands,
    private val dispatchers: AppDispatchers,
    private val componentScope: CoroutineScope,
    private val localization: LocalizationResource,
    private val torrServerStarter: TorrServerStarterPlatform
) : TorrserverBarComponent, ComponentContext by context {

    private val _uiState = MutableStateFlow(TorrserverBarState())
    override val uiState: StateFlow<TorrserverBarState> = _uiState.asStateFlow()


    override fun onClickInstallServer() {
        _uiState.update { it.copy(isShowProgress = true) }
        componentScope.launch(dispatchers.defaultDispatcher()) {
            torrserverCommands.installServer().collect { result ->
                when (result) {
                    is ResultProgress.Loading -> showProgress(result.progress.progress.toFloat())
                    is ResultProgress.Error -> showError(result.error)
                    is ResultProgress.Success -> serverInstalled()
                }
            }
        }
    }

    private suspend fun showProgress(progress: Float) {
        _uiState.update {
            it.copy(
                isShowProgress = true,
                progressValue = progress,
                serverStatusText = localization.getString(Res.string.main_torrserver_bar_msg_installing_torrserver),
            )
        }
    }

    private suspend fun showError(error: TorrserverError) {
        _uiState.update { it.copy(isShowProgress = false, error = error.toMessage(localization)) }
    }

    private suspend fun serverInstalled() {
        _uiState.update { it.copy(isShowProgress = false, isServerInstalled = true) }
        torrServerStarter.startTorrServer()
    }

    override fun onClickStartServer() {
        componentScope.launch(dispatchers.defaultDispatcher()) {
            torrServerStarter.startTorrServer()
        }
    }
}