package com.dik.torrentlist.screens.main.appbar

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.StateFlow
import org.jetbrains.compose.resources.StringResource

internal interface MainAppBarComponent {

    val uiState: StateFlow<MainAppBarState>

    fun onClickAddTorrent()
    fun openAddLinkDialog()
    fun addLink()
    fun dismissDialog()
    fun onLinkChaged(value: String)
    fun clearLink()
    fun openSettingsScreen()
}

@Stable
internal data class MainAppBarState(
    val action: MainAppBarAction = MainAppBarAction.Undefined,
    val link: String = "",
    val errorLink: StringResource? = null,
    val isServerStarted: Boolean = false,
)

internal sealed interface MainAppBarAction {
    object ShowAddLinkDialog : MainAppBarAction
    object Undefined: MainAppBarAction
}