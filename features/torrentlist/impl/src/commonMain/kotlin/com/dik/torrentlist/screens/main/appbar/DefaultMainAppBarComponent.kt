package com.dik.torrentlist.screens.main.appbar

import com.arkivanov.decompose.ComponentContext
import com.dik.common.i18n.LocalizationResource
import com.dik.torrentlist.screens.main.AddMagnetLink
import com.dik.torrentlist.screens.main.AddTorrentFile
import com.dik.torrentlist.utils.FileUtils
import com.dik.torrserverapi.model.TorrserverStatus
import com.dik.torrserverapi.server.TorrserverCommands
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import torrservermedia.features.torrentlist.impl.generated.resources.Res
import torrservermedia.features.torrentlist.impl.generated.resources.main_app_bar_error_server_not_started

internal class DefaultMainAppBarComponent(
    context: ComponentContext,
    private val componentScope: CoroutineScope,
    private val addTorrentFile: AddTorrentFile,
    private val addMagnetLink: AddMagnetLink,
    private val torrserverCommands: TorrserverCommands,
    private val localization: LocalizationResource,
    private val fileUtils: FileUtils,
    private val openSettingsScreen: () -> Unit,
) : MainAppBarComponent, ComponentContext by context {

    private val _uiState = MutableStateFlow(MainAppBarState())
    override val uiState: StateFlow<MainAppBarState> = _uiState.asStateFlow()

    init {
        observeServerStatus()
    }

    override fun openFilePickTorrent() {
        if (!_uiState.value.isServerStarted) {
            componentScope.launch {
                _uiState.update { it.copy(error = localization.getString(Res.string.main_app_bar_error_server_not_started)) }
            }

            return
        }

        _uiState.update { it.copy(action = MainAppBarAction.ShowFilePicker) }
    }

    override fun onFilePicked(path: String) {
        componentScope.launch {
            _uiState.update { it.copy(action = MainAppBarAction.Undefined) }
            val absolutePath = fileUtils.absolutPath(path)
            addTorrentFile.invoke(absolutePath)
        }
    }

    override fun openAddLinkDialog() {
        if (!_uiState.value.isServerStarted) {
            componentScope.launch {
                _uiState.update { it.copy(error = localization.getString(Res.string.main_app_bar_error_server_not_started)) }
            }

            return
        }

        _uiState.update { it.copy(action = MainAppBarAction.ShowAddLinkDialog) }
    }

    override fun addLink() {
        componentScope.launch {
            if (_uiState.value.link.isEmpty()) return@launch

            val result = addMagnetLink.invoke(_uiState.value.link)

            if (!result.error.isNullOrEmpty()) {
                _uiState.update { it.copy(errorLink = result.error) }
                return@launch
            }

            dismissDialog()
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
        if (!_uiState.value.isServerStarted) {
            componentScope.launch {
                _uiState.update {
                    it.copy(error = localization.getString(Res.string.main_app_bar_error_server_not_started))
                }
            }

            return
        }

        openSettingsScreen.invoke()
    }

    private fun observeServerStatus() {
        componentScope.launch {
            torrserverCommands.serverStatus().collect { status ->
                _uiState.update { it.copy(isServerStarted = status == TorrserverStatus.STARTED) }
            }
        }
    }
}