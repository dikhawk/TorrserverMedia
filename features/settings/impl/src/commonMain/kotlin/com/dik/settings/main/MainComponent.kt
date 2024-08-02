package com.dik.settings.main

import androidx.compose.runtime.Stable
import com.dik.common.Players
import kotlinx.coroutines.flow.StateFlow

internal interface MainComponent {

    val uiState: StateFlow<MainState>

    fun onClickBack()
    fun onChangeIpv6(checked: Boolean)
    fun onChangeTcp(checked: Boolean)
    fun onChangeMtp(checked: Boolean)
    fun onChangePex(checked: Boolean)
    fun onChangeEncryptionHeader(checked: Boolean)
    fun onChangeTimeoutConnection(value: String)
    fun onChangeTorrentConnections(value: String)
    fun onChangeDht(checked: Boolean)
    fun onChangeLimitSpeedDownload(value: String)
    fun onChangeIncomingConnection(value: String)
    fun onChangeDistribution(checked: Boolean)
    fun onChangeUpnp(checked: Boolean)
    fun onChangeDlna(checked: Boolean)
    fun onChangeDlnaName(value: String)
    fun onChangeLimitSpeedDistribution(value: String)
    fun onClickSave()
    fun dismissSnackbar()
    fun invokeAction(action: MainAction)
    fun dismissAction()
    fun defaultSettings()
    fun onChangeCacheSize(value: String)
    fun onChangeReaderReadAHead(value: String)
    fun onChangePreloadCache(value: String)
    fun onChangeDefaultPlayer(value: Players)
}

@Stable
internal data class MainState(
    val isShowProgressBar: Boolean = false,
    val deafaultPlayer: Players = Players.SYSTEM_DEFULT_PLAYER,
    val playersList: List<Players> = listOf(),
    val cacheSize: String = "0",
    val readerReadAHead: String = "0",
    val preloadCache: String = "0",
    val ipv6: Boolean = false,
    val tcp: Boolean = false,
    val mtp: Boolean = false,
    val pex: Boolean = false,
    val encryptionHeader: Boolean = false,
    val timeoutConnection: String = "0",
    val torrentConnections: String = "0",
    val dht: Boolean = false,
    val limitSpeedDownload: String = "0",
    val distribution: Boolean = false,
    val limitSpeedDistribution: String = "0",
    val incomingConnection: String = "0",
    val upnp: Boolean = false,
    val dlna: Boolean = false,
    val dlnaName: String = "",
    val snackbar: String? = null,
    val action: MainAction? = null
)

internal enum class MainAction {
    DEFAULT_SETTINGS_DIALOG,
}