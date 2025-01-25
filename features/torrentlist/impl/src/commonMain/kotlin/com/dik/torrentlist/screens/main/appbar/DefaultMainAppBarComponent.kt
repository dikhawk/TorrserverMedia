package com.dik.torrentlist.screens.main.appbar

import com.arkivanov.decompose.ComponentContext
import com.dik.common.AppDispatchers
import com.dik.torrentlist.screens.main.AddMagnetLink
import com.dik.torrentlist.screens.main.AddTorrentFile
import com.dik.torrentlist.screens.main.appbar.utils.defaultFilePickerDirectory
import com.dik.torrserverapi.model.TorrserverStatus
import com.dik.torrserverapi.server.TorrserverCommands
import io.github.vinceglb.filekit.core.FileKit
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType
import io.github.vinceglb.filekit.core.PlatformFile
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import torrservermedia.features.torrentlist.impl.generated.resources.Res
import torrservermedia.features.torrentlist.impl.generated.resources.add_torrent_dialog_title

internal class DefaultMainAppBarComponent(
    context: ComponentContext,
    private val dispatchers: AppDispatchers,
    private val componentScope: CoroutineScope,
    private val addTorrentFile: AddTorrentFile,
    private val addMagnetLink: AddMagnetLink,
    private val torrserverCommands: TorrserverCommands,
    private val openSettingsScreen: () -> Unit,
) : MainAppBarComponent, ComponentContext by context {

    private val _uiState = MutableStateFlow(MainAppBarState())
    override val uiState: StateFlow<MainAppBarState> = _uiState.asStateFlow()

    init {
        observeServerStatus()
    }

    override fun onClickAddTorrent() {
        if (!_uiState.value.isServerStarted) return

        componentScope.launch(dispatchers.defaultDispatcher()) {
            val fileType = PickerType.File(extensions = listOf("torrent"))

            val file = FileKit.pickFile(
                type = fileType,
                mode = PickerMode.Single,
                title = getString(Res.string.add_torrent_dialog_title),
                initialDirectory = defaultFilePickerDirectory()
            )
            val filePath = file?.absolutePath(dispatchers.ioDispatcher())

            if (filePath != null) {
                addTorrentFile.invoke(filePath)
            }
        }
    }

    override fun openAddLinkDialog() {
        if (!_uiState.value.isServerStarted) return

        _uiState.update { it.copy(action = MainAppBarAction.ShowAddLinkDialog) }
    }

    override fun addLink() {
        componentScope.launch(dispatchers.ioDispatcher()) {
            val result = addMagnetLink.invoke(_uiState.value.link)

            if (!result.error.isNullOrEmpty()) {
                _uiState.update { it.copy(errorLink = result.error) }
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

    override fun clearLink() {
        _uiState.update { it.copy(link = "", errorLink = null) }
    }

    override fun openSettingsScreen() {
        if (!_uiState.value.isServerStarted) return

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

internal expect suspend fun PlatformFile.absolutePath(dispatcher: CoroutineDispatcher = Dispatchers.IO): String?