package com.dik.settings.main

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.dik.appsettings.api.model.AppSettings
import com.dik.common.AppDispatchers
import com.dik.common.Result
import com.dik.common.i18n.AppLanguage
import com.dik.common.i18n.LocalizationResource
import com.dik.common.i18n.setLocalization
import com.dik.common.onError
import com.dik.common.onSuccess
import com.dik.common.player.Player
import com.dik.common.utils.cpuArch
import com.dik.common.utils.platformName
import com.dik.settings.utils.bytesToMb
import com.dik.settings.utils.mbToBytes
import com.dik.settings.utils.toIntOrZero
import com.dik.settings.utils.toLongOrZero
import com.dik.torrserverapi.model.ServerSettings
import com.dik.torrserverapi.server.TorrserverManager
import com.dik.torrserverapi.server.TorrserverStatus
import com.dik.torrserverapi.server.api.ServerSettingsApi
import com.dik.torrserverapi.server.api.TorrserverApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import torrservermedia.features.settings.impl.generated.resources.Res
import torrservermedia.features.settings.impl.generated.resources.main_settings_snackbar_default_settings
import torrservermedia.features.settings.impl.generated.resources.main_settings_snackbar_not_available_now
import torrservermedia.features.settings.impl.generated.resources.main_settings_snackbar_save

internal class DefaultMainComponent(
    context: ComponentContext,
    private val serverSettingsApi: ServerSettingsApi,
    private val torrserverApiClient: TorrserverApiClient,
    private val torrserverManager: TorrserverManager,
    private val appSettings: AppSettings,
    private val localization: LocalizationResource,
    private val dispatchers: AppDispatchers,
    private val onFinish: () -> Unit,
) : MainComponent, ComponentContext by context {

    private val componentScope = CoroutineScope(dispatchers.mainDispatcher() + SupervisorJob())

    private val _uiState = MutableStateFlow(MainState())
    override val uiState: StateFlow<MainState> = _uiState.asStateFlow().onStart {
        if (!_uiState.value.isLoadedData) {
            loadSettings()
        }
        checkUpdates()
    }.stateIn(
        scope = componentScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = _uiState.value
    )

    init {
        lifecycle.doOnDestroy { componentScope.cancel() }
    }

    override fun onClickBack() {
        onFinish.invoke()
    }

    override fun onClickUpdateTorrserver() {
        _uiState.update { it.copy(serverVersionState = ServerVersionState.PreparingUpdate) }
        componentScope.launch {
            torrserverManager.installOrUpdate()
                .distinctUntilChanged()
                .collect { result ->
                    _uiState.update { it.copy(serverVersionState = result.toServerVersionState()) }

                    if (result is TorrserverStatus.Install.Installed) {
                        restartTorrserver()
                    }
                }
        }
    }

    private suspend fun restartTorrserver() {
        torrserverManager.restart().last()
    }

    override fun onChangeDefaultPlayer(value: Player) {
        _uiState.update { it.copy(player = value) }
    }

    override fun onChangeCacheSize(value: String) {
        if (value.all { it.isDigit() })
            _uiState.update { it.copy(cacheSize = value) }
    }

    override fun onChangeReaderReadAHead(value: String) {
        if (value.all { it.isDigit() })
            _uiState.update { it.copy(readerReadAHead = value) }
    }

    override fun onChangePreloadCache(value: String) {
        if (value.all { it.isDigit() }) {
            _uiState.update { it.copy(preloadCache = value) }
        }
    }

    override fun onChangeIpv6(checked: Boolean) {
        _uiState.update { it.copy(ipv6 = checked) }
    }

    override fun onChangeTcp(checked: Boolean) {
        _uiState.update { it.copy(tcp = checked) }
    }

    override fun onChangeMtp(checked: Boolean) {
        _uiState.update { it.copy(mtp = checked) }
    }

    override fun onChangePex(checked: Boolean) {
        _uiState.update { it.copy(pex = checked) }
    }

    override fun onChangeEncryptionHeader(checked: Boolean) {
        _uiState.update { it.copy(encryptionHeader = checked) }
    }

    override fun onChangeTimeoutConnection(value: String) {
        if (value.all { it.isDigit() })
            _uiState.update { it.copy(timeoutConnection = value) }
    }

    override fun onChangeTorrentConnections(value: String) {
        if (value.all { it.isDigit() })
            _uiState.update { it.copy(torrentConnections = value) }
    }

    override fun onChangeDht(checked: Boolean) {
        _uiState.update { it.copy(dht = checked) }
    }

    override fun onChangeLimitSpeedDownload(value: String) {
        if (value.all { it.isDigit() })
            _uiState.update { it.copy(limitSpeedDownload = value) }
    }

    override fun onChangeIncomingConnection(value: String) {
        if (value.all { it.isDigit() })
            _uiState.update { it.copy(incomingConnection = value) }
    }

    override fun onChangeDistribution(checked: Boolean) {
        _uiState.update { it.copy(distribution = checked) }
    }

    override fun onChangeUpnp(checked: Boolean) {
        _uiState.update { it.copy(upnp = checked) }
    }

    override fun onChangeDlna(checked: Boolean) {
        _uiState.update { it.copy(dlna = checked) }
    }

    override fun onChangeLimitSpeedDistribution(value: String) {
        if (value.all { it.isDigit() })
            _uiState.update { it.copy(limitSpeedDistribution = value) }
    }

    override fun onChangeDlnaName(value: String) {
        _uiState.update { it.copy(dlnaName = value) }
    }

    override fun onChangeLanguage(value: AppLanguage) {
        componentScope.launch {
            _uiState.update { it.copy(language = value) }
            setLocalization(value)
        }
    }

    override fun onClickSave() {
        if (_uiState.value.isShowProgressBar) {
            componentScope.launch {
                val message =
                    localization.getString(Res.string.main_settings_snackbar_not_available_now)
                _uiState.update { it.copy(snackbar = message) }
            }
            return
        }

        showProgressBar()

        componentScope.launch {
            appSettings.defaultPlayer = _uiState.value.player
            appSettings.language = _uiState.value.language

            serverSettingsApi.saveSettings(getServerSettings()).onSuccess {
                _uiState.update {
                    it.copy(snackbar = localization.getString(Res.string.main_settings_snackbar_save))
                }
            }.onError { error ->
                _uiState.update { it.copy(snackbar = error.toString()) }
            }

            hideProgressBar()
        }
    }

    private fun getServerSettings(): ServerSettings {
        val cacheSize = _uiState.value.cacheSize.toLongOrZero().mbToBytes()
        val readerReadAHead = _uiState.value.readerReadAHead.toIntOrZero()
        val preloadCache = _uiState.value.preloadCache.toIntOrZero()
        val timeoutConnection = _uiState.value.timeoutConnection.toIntOrZero()
        val torrentConnections = _uiState.value.torrentConnections.toIntOrZero()
        val limitSpeedDownload = _uiState.value.limitSpeedDownload.toIntOrZero()
        val incomingConnection = _uiState.value.incomingConnection.toIntOrZero()
        val limitSpeedDistribution = _uiState.value.limitSpeedDistribution.toIntOrZero()

        return ServerSettings(
            cacheSize = cacheSize,
            readerReadAHead = readerReadAHead,
            preloadCache = preloadCache,
            ipv6 = _uiState.value.ipv6,
            tcp = _uiState.value.tcp,
            mtp = _uiState.value.mtp,
            pex = _uiState.value.pex,
            encryptionHeader = _uiState.value.encryptionHeader,
            timeoutConnection = timeoutConnection,
            torrentConnections = torrentConnections,
            dht = _uiState.value.dht,
            upnp = _uiState.value.upnp,
            limitSpeedDownload = limitSpeedDownload,
            incomingConnection = incomingConnection,
            distribution = _uiState.value.distribution,
            limitSpeedDistribution = limitSpeedDistribution,
            dlna = _uiState.value.dlna,
            dlnaName = _uiState.value.dlnaName,
        )
    }

    override fun dismissSnackbar() {
        _uiState.update { it.copy(snackbar = null) }
    }

    override fun invokeAction(action: MainAction) {
        _uiState.update { it.copy(action = action) }
    }

    override fun dismissAction() {
        _uiState.update { it.copy(action = null) }
    }

    override fun defaultSettings() {
        if (_uiState.value.isShowProgressBar) {
            componentScope.launch {
                _uiState.update { it.copy(snackbar = localization.getString(Res.string.main_settings_snackbar_not_available_now)) }
            }
            return
        }

        showProgressBar()
        componentScope.launch {
            val result = serverSettingsApi.defaultSettings()
            when (result) {
                is Result.Error -> showError(result.error.toString())
                is Result.Success -> {
                    updateSettingsUiState(result.data)
                    _uiState.update { it.copy(snackbar = localization.getString(Res.string.main_settings_snackbar_default_settings)) }
                }
            }
            hideProgressBar()
        }
    }

    private fun loadSettings() {
        showProgressBar()

        componentScope.launch {
            serverSettingsApi.getSettings().onSuccess { settings ->
                updateSettingsUiState(settings)
            }.onError { error ->
                showError(error.toString())
            }
            hideProgressBar()
        }
    }

    override fun showProgressBar() {
        _uiState.update { it.copy(isShowProgressBar = true) }
    }

    override fun hideProgressBar() {
        _uiState.update { it.copy(isShowProgressBar = false) }
    }

    private suspend fun checkUpdates() {
        torrserverManager.checkNewVersion().collect { status ->
            _uiState.update { it.copy(serverVersionState = status.toServerVersionState()) }
/*            when (status) {
                is TorrserverStatus.CheckLatestVersion.AvailableNewVersion -> {
                    _uiState.update {
                        it.copy(
                            serverVersionState = ServerVersionState.AvailableNewVersion(status.msg),
                            availableNewVersionText = localization.getString(Res.string.main_settings_available_new_version),
                            isAvailableNewVersion = true
                        )
                    }
                }

                is TorrserverStatus.CheckLatestVersion.VersionIsActual -> {

                }

                is TorrserverStatus.CheckLatestVersion.Checking -> {

                }

                else -> println("Status: $status")
            }*/
        }
    }

    private fun updateSettingsUiState(settings: ServerSettings) {
        val platform = platformName()
        val cpu = cpuArch()

        _uiState.update {
            it.copy(
                isLoadedData = true,
                operationSystem = "${platform.osname} $cpu",
                player = appSettings.defaultPlayer,
                language = appSettings.language,
                playersList = getPlayersLists(),
                cacheSize = settings.cacheSize.bytesToMb().toString(), //to Mb
                readerReadAHead = settings.readerReadAHead.toString(),
                preloadCache = settings.preloadCache.toString(),
                ipv6 = settings.ipv6,
                tcp = settings.tcp,
                mtp = settings.mtp,
                pex = settings.pex,
                encryptionHeader = settings.encryptionHeader,
                timeoutConnection = settings.timeoutConnection.toString(),
                torrentConnections = settings.torrentConnections.toString(),
                dht = settings.dht,
                upnp = settings.upnp,
                limitSpeedDownload = settings.limitSpeedDownload.toString(),
                incomingConnection = settings.incomingConnection.toString(),
                distribution = settings.distribution,
                limitSpeedDistribution = settings.limitSpeedDistribution.toString(),
                dlna = settings.dlna,
                dlnaName = settings.dlnaName,
            )
        }
    }

    private fun getPlayersLists(): List<Player> {
        return Player.entries.filter { it.platforms.contains(platformName()) }
    }

    private fun showError(message: String) {
        _uiState.update { it.copy(snackbar = message) }
    }
}