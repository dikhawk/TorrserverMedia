package com.dik.torrentlist.screens.main.torrserverbar

import com.arkivanov.decompose.ComponentContext
import com.dik.common.AppDispatchers
import com.dik.common.converter.toReadableSize
import com.dik.torrserverapi.server.TorrserverManager
import com.dik.torrserverapi.server.TorrserverStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class DefaultTorrserverBarComponent(
    context: ComponentContext,
    private val torrserverManager: TorrserverManager,
    private val dispatchers: AppDispatchers,
    private val componentScope: CoroutineScope,
) : TorrserverBarComponent, ComponentContext by context {

    private val _uiState = MutableStateFlow(TorrserverBarState())
    override val uiState: StateFlow<TorrserverBarState> = _uiState.asStateFlow()

    override fun onClickInstallServer() {
        showPreparing()
        componentScope.launch(dispatchers.defaultDispatcher()) {
            torrserverManager.installOrUpdate().collect { result ->
                when (result) {
                    is TorrserverStatus.Install.Progress ->
                        showInstallingProgress(
                            result.progress,
                            result.currentBytes,
                            result.totalBytes
                        )

                    is TorrserverStatus.Install.Error -> showError(result.msg)
                    is TorrserverStatus.Install.Installed -> serverInstalled()
                    else -> println("Server status: $result")
                }
            }
        }
    }

    private fun showPreparing() {
        _uiState.update {
            it.copy(
                installingState = InstallingState.Preparing,
                reinstallingState = InstallingState.Preparing
            )
        }
    }

    private fun showInstallingProgress(
        progress: Double,
        currentBytes: Long,
        totalBytes: Long,
    ) {
        val state = InstallingState.Installing(
            progress = progress.toFloat() / 100f,
            percent = progress.toString(),
            currentBytes.toReadableSize(),
            totalBytes.toReadableSize()
        )
        _uiState.update {
            it.copy(installingState = state, reinstallingState = state)
        }
    }

    private fun showError(error: String) {
        val state = InstallingState.Error(error)
        _uiState.update { it.copy(installingState = state, reinstallingState = state) }
    }

    private suspend fun serverInstalled() {
        val state = InstallingState.Installed
        _uiState.update { it.copy(installingState = state, reinstallingState = state) }
        torrserverManager.start().last()
    }

    override fun onClickStartServer() {
        componentScope.launch(dispatchers.defaultDispatcher()) {
            torrserverManager.start().last()
        }
    }
}