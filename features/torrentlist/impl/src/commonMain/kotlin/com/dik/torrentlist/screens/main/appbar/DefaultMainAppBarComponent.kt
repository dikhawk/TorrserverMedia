package com.dik.torrentlist.screens.main.appbar

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.dik.common.AppDispatchers
import com.dik.torrentlist.screens.main.appbar.utils.defaultFilePickerDirectory
import com.dik.torrserverapi.server.MagnetApi
import com.dik.torrserverapi.server.TorrentApi
import io.github.vinceglb.filekit.core.FileKit
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import torrservermedia.features.torrentlist.impl.generated.resources.Res
import torrservermedia.features.torrentlist.impl.generated.resources.main_add_dialog_error_invalid_magnet
import torrservermedia.features.torrentlist.impl.generated.resources.main_app_bar_title

internal class DefaultMainAppBarComponent(
    context: ComponentContext,
    private val dispatchers: AppDispatchers,
    private val torrentApi: TorrentApi,
    private val magnetApi: MagnetApi,
    private val openSettingsScreen: () -> Unit,
) : MainAppBarComponent, ComponentContext by context {

    private val componentScope = CoroutineScope(dispatchers.mainDispatcher() + SupervisorJob())
    private val _uiState = MutableStateFlow(MainAppBarState())
    override val uiState: StateFlow<MainAppBarState> = _uiState.asStateFlow()


    init {
        lifecycle.doOnDestroy { componentScope.cancel() }
    }

    override fun onClickAddTorrent() {
        componentScope.launch(dispatchers.defaultDispatcher()) {
            val fileType = PickerType.File(extensions = listOf("torrent"))

            val file = FileKit.pickFile(
                type = fileType,
                mode = PickerMode.Single,
                title = getString(Res.string.main_app_bar_title),
                initialDirectory = defaultFilePickerDirectory()
            )
            val filePath = file?.path

            if (filePath != null) torrentApi.addTorrent(filePath)
        }
    }

    override fun openAddLinkDialog() {
        _uiState.update { it.copy(event = MainAppBarEvent.ShowAddLinkDialog) }
    }

    override fun addLink() {
        if (!isValidMagnetLink(_uiState.value.link)) {
            _uiState.update { it.copy(errorLink = Res.string.main_add_dialog_error_invalid_magnet) }
            return
        }

        componentScope.launch(dispatchers.ioDispatcher()) {
            magnetApi.addMagnet(magnetUrl = _uiState.value.link)
            dismissDialog()
        }
    }

    override fun dismissDialog() {
        _uiState.update { it.copy(event = MainAppBarEvent.Undefined) }
        clearLink()
    }

    override fun onLinkChaged(value: String) {
        _uiState.update { it.copy(link = value, errorLink = null) }
    }

    override fun clearLink() {
        _uiState.update { it.copy(link = "", errorLink = null) }
    }

    override fun openSettingsScreen() {
        openSettingsScreen.invoke()
    }

    private fun isValidMagnetLink(magnetLink: String): Boolean {
        val magnetUriRegex = Regex("^magnet:\\?xt=urn:[a-z0-9]+:[a-zA-Z0-9]{32,40}(&.+)?$")
        return magnetUriRegex.matches(magnetLink)
    }
}