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
    val event: MainAppBarEvent = MainAppBarEvent.Undefined,
    val link: String = "",
    val errorLink: StringResource? = null
)

internal sealed interface MainAppBarEvent {
    object ShowAddLinkDialog : MainAppBarEvent
    object Undefined: MainAppBarEvent
}