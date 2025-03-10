package com.dik.torrentlist.screens.main.appbar

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.StateFlow

internal interface MainAppBarComponent {

    val uiState: StateFlow<MainAppBarState>

    fun openFilePickTorrent()
    fun onFilePicked(path: String)
    fun openAddLinkDialog()
    fun addLink()
    fun dismissDialog()
    fun onLinkChanged(value: String)
    fun clearLink()
    fun openSettingsScreen()
    fun dismissAction()
}

@Stable
internal data class MainAppBarState(
    val action: MainAppBarAction = MainAppBarAction.Undefined,
    val link: String = "",
    val errorLink: String? = null,
    val isServerStarted: Boolean = false,
    val error: String? = null
)

internal sealed interface MainAppBarAction {
    data object ShowAddLinkDialog : MainAppBarAction
    data object ShowFilePicker : MainAppBarAction
    data object Undefined: MainAppBarAction
}