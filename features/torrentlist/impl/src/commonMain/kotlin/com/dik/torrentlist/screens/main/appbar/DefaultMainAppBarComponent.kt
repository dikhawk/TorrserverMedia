package com.dik.torrentlist.screens.main.appbar

import com.arkivanov.decompose.ComponentContext
import com.dik.common.i18n.LocalizationResource
import com.dik.common.onError
import com.dik.common.onSuccess
import com.dik.torrentlist.screens.main.domain.AddMagnetLinkUseCase
import com.dik.torrentlist.screens.main.domain.AddTorrentFileUseCase
import com.dik.torrentlist.utils.FileUtils
import com.dik.torrserverapi.server.TorrserverManager
import com.dik.torrserverapi.server.TorrserverStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import torrservermedia.features.torrentlist.impl.generated.resources.Res
import torrservermedia.features.torrentlist.impl.generated.resources.main_app_bar_error_server_not_started

internal class DefaultMainAppBarComponent(
    context: ComponentContext,
    private val componentScope: CoroutineScope,
    private val addTorrentFileUseCase: AddTorrentFileUseCase,
    private val addMagnetLinkUseCase: AddMagnetLinkUseCase,
    private val torrserverManager: TorrserverManager,
    private val localization: LocalizationResource,
    private val fileUtils: FileUtils,
    private val openSettingsScreen: () -> Unit,
) : MainAppBarComponent, ComponentContext by context {

    private val observeTorrserverStatus = torrserverManager.observeTorrserverStatus()
        .distinctUntilChanged()

    private val _uiState = MutableStateFlow(MainAppBarState())
    override val uiState: StateFlow<MainAppBarState> = combine(
        observeTorrserverStatus,
        _uiState
    ) { status, state ->
        val isStarted = status == TorrserverStatus.General.Started
        state.copy(isServerStarted = isStarted)
    }.stateIn(
        scope = componentScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = _uiState.value
    )

    override fun openFilePickTorrent() {
        if (!uiState.value.isServerStarted) {
            componentScope.launch {
                _uiState.update {
                    it.copy(error = localization.getString(Res.string.main_app_bar_error_server_not_started))
                }
            }

            return
        }

        _uiState.update { it.copy(action = MainAppBarAction.ShowFilePicker) }
    }

    override fun onFilePicked(path: String) {
        componentScope.launch {
            _uiState.update { it.copy(action = MainAppBarAction.Undefined) }
            val absolutePath = fileUtils.absolutPath(path)
            addTorrentFileUseCase.invoke(absolutePath)
        }
    }

    override fun openAddLinkDialog() {
        if (!uiState.value.isServerStarted) {
            componentScope.launch {
                _uiState.update {
                    it.copy(error = localization.getString(Res.string.main_app_bar_error_server_not_started))
                }
            }

            return
        }

        _uiState.update { it.copy(action = MainAppBarAction.ShowAddLinkDialog) }
    }

    override fun addLink() {
        componentScope.launch {
            if (uiState.value.link.isEmpty()) return@launch

            addMagnetLinkUseCase.invoke(_uiState.value.link)
                .onError { error ->
                    _uiState.update { it.copy(errorLink = error.toString()) }
                }.onSuccess {
                    dismissDialog()
                }
        }
    }

    override fun dismissDialog() {
        _uiState.update { it.copy(action = MainAppBarAction.Undefined) }
        clearLink()
    }

    override fun onLinkChanged(value: String) {
        _uiState.update { it.copy(link = value, errorLink = null) }
    }

    override fun dismissAction() {
        _uiState.update { it.copy(action = MainAppBarAction.Undefined) }
    }

    override fun clearLink() {
        _uiState.update { it.copy(link = "", errorLink = null) }
    }

    override fun openSettingsScreen() {
        if (!uiState.value.isServerStarted) {
            componentScope.launch {
                _uiState.update {
                    it.copy(error = localization.getString(Res.string.main_app_bar_error_server_not_started))
                }
            }

            return
        }

        openSettingsScreen.invoke()
    }
}