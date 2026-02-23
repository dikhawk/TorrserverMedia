package com.dik.torrentlist.screens.main

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.dik.appsettings.api.model.AppSettings
import com.dik.common.AppDispatchers
import com.dik.common.Result
import com.dik.common.i18n.LocalizationResource
import com.dik.common.onError
import com.dik.common.onSuccess
import com.dik.common.utils.repeatIf
import com.dik.common.utils.successResult
import com.dik.themoviedb.SearchTheMovieDbApi
import com.dik.themoviedb.TvEpisodesTheMovieDbApi
import com.dik.themoviedb.TvSeasonsTheMovieDbApi
import com.dik.torrentlist.di.inject
import com.dik.torrentlist.screens.components.bufferization.BufferizationComponent
import com.dik.torrentlist.screens.components.bufferization.DefaultBufferizationComponent
import com.dik.torrentlist.screens.details.DefaultDetailsComponent
import com.dik.torrentlist.screens.details.DetailsComponentScreenFormat
import com.dik.torrentlist.screens.main.appbar.DefaultMainAppBarComponent
import com.dik.torrentlist.screens.main.appbar.MainAppBarComponent
import com.dik.torrentlist.screens.main.domain.AddMagnetLinkUseCase
import com.dik.torrentlist.screens.main.domain.AddTorrentFileUseCase
import com.dik.torrentlist.screens.main.domain.FindPosterUseCase
import com.dik.torrentlist.screens.main.list.DefaultTorrentListComponent
import com.dik.torrentlist.screens.main.list.TorrentListComponent
import com.dik.torrentlist.screens.main.torrserverbar.DefaultTorrserverBarComponent
import com.dik.torrentlist.screens.main.torrserverbar.TorrserverBarComponent
import com.dik.torrentlist.screens.mappers.toTorrentUiState
import com.dik.torrentlist.screens.model.ContentFileUiState
import com.dik.torrentlist.screens.model.TorrentUiState
import com.dik.torrentlist.utils.FileUtils
import com.dik.torrserverapi.server.TorrserverManager
import com.dik.torrserverapi.server.TorrserverStatus
import com.dik.torrserverapi.server.api.TorrentApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class DefaultMainComponent(
    context: ComponentContext,
    private val torrentApi: TorrentApi = inject(),
    private val dispatchers: AppDispatchers = inject(),
    private val torrserverManager: TorrserverManager = inject(),
    private val searchingTmdb: SearchTheMovieDbApi = inject(),
    private val addTorrentFileUseCase: AddTorrentFileUseCase = inject(),
    private val addMagnetLinkUseCase: AddMagnetLinkUseCase = inject(),
    private val tvEpisodesTmdb: TvEpisodesTheMovieDbApi = inject(),
    private val tvSeasonTmdb: TvSeasonsTheMovieDbApi = inject(),
    private val appSettings: AppSettings = inject(),
    private val localization: LocalizationResource = inject(),
    private val findPosterUseCase: FindPosterUseCase = inject(),
    private val fileUtils: FileUtils = inject(),
    private val openSettingsScreen: () -> Unit = {},
    private val onClickPlayFile: suspend (contentFile: ContentFileUiState) -> Unit,
    private val navigateToDetails: (torrentHash: String, poster: String) -> Unit
) : MainComponent, ComponentContext by context {

    private val _uiState = MutableStateFlow(MainComponentState())
    override val uiState: StateFlow<MainComponentState> = _uiState.asStateFlow()

    private val componentScope = CoroutineScope(dispatchers.defaultDispatcher() + SupervisorJob())

    init {
        lifecycle.doOnDestroy { componentScope.cancel() }
        observeServerStatus()
    }

    override val mainAppBarComponent: MainAppBarComponent = DefaultMainAppBarComponent(
        context = childContext("main_app_bar"),
        componentScope = componentScope,
        openSettingsScreen = openSettingsScreen,
        torrserverManager = torrserverManager,
        addTorrentFileUseCase = addTorrentFileUseCase,
        addMagnetLinkUseCase = addMagnetLinkUseCase,
        localization = localization,
        fileUtils = fileUtils
    )

    override val torrserverBarComponent: TorrserverBarComponent =
        DefaultTorrserverBarComponent(
            context = childContext("torrserver_bar"),
            torrserverManager = torrserverManager,
            dispatchers = dispatchers,
            componentScope = componentScope,
            localization = localization,
        )

    override val torrentListComponent: TorrentListComponent = DefaultTorrentListComponent(
        context = childContext("torrserverbar"),
        onTorrentClick = ::showDetails,
        onNavigateToDetails = ::navigateToDetails,
        onTorrentsIsEmpty = { isEmpty ->
            if (isEmpty) _uiState.update { it.copy(isShowDetails = false) }
        },
        torrentApi = torrentApi,
        componentScope = componentScope,
        addTorrentFileUseCase = addTorrentFileUseCase,
        addMagnetLinkUseCase = addMagnetLinkUseCase,
        fileUtils = fileUtils
    )

    private fun showDetails(torrent: TorrentUiState) {
        detailsComponent.showDetails(torrent.hash)
        _uiState.update { it.copy(isShowDetails = true) }
    }

    private fun navigateToDetails(torrent: TorrentUiState) {
        navigateToDetails(torrent.hash, torrent.poster)
    }

    private fun playFile(contentFile: ContentFileUiState) {
        componentScope.launch(dispatchers.defaultDispatcher()) {
            onClickPlayFile(contentFile)
        }
    }

    override val detailsComponent =
        DefaultDetailsComponent(
            componentContext = childContext(("details")),
            dispatchers = dispatchers,
            torrentApi = torrentApi,
            appSettings = appSettings,
            searchingTmdb = searchingTmdb,
            tvSeasonTmdb = tvSeasonTmdb,
            tvEpisodesTmdb = tvEpisodesTmdb,
            localization = localization,
            findPosterUseCase = findPosterUseCase,
            screenFormat = DetailsComponentScreenFormat.PANE,
            onClickPlayFile = { torrent, contentFile ->
                _uiState.update { it.copy(isShowDetails = true, isShowBufferization = true) }
                bufferizationComponent.startBufferization(
                    torrent = torrent,
                    contentFile = contentFile,
                    runAfterBufferization = { playFile(contentFile) }
                )
            }
        )

    override val bufferizationComponent: BufferizationComponent =
        DefaultBufferizationComponent(
            componentContext = childContext("bufferization"),
            dispatchers = dispatchers,
            componentScope = componentScope,
            torrentApi = torrentApi,
            searchTheMovieDbApi = searchingTmdb,
            tvEpisodesTheMovieDbApi = tvEpisodesTmdb,
            localization = localization,
            appSettings = appSettings,
            onClickDismiss = {
                _uiState.update {
                    it.copy(isShowBufferization = false)
                }
            }
        )

    private fun observeServerStatus() {
        componentScope.launch {
            torrserverManager.observeTorrserverStatus().collect { status ->
                _uiState.update { it.copy(serverStatus = status) }
            }
        }
    }

    fun addTorrentAndShowDetails(pathToTorrent: String) {
        componentScope.launch(dispatchers.mainDispatcher()) {
            suspend {
                addTorrentFileUseCase.invoke(pathToTorrent)
            }.repeatIf(maxTries = 15) {
                _uiState.value.serverStatus != TorrserverStatus.General.Started
            }?.onSuccess { torrent ->
                showDetails(torrent.toTorrentUiState())
            }?.onError { error ->
                _uiState.update { it.copy(error = error.toString()) }
            }
        }
    }

    fun addMagnetLinkAndShowDetails(magnetLink: String) {
        componentScope.launch(dispatchers.mainDispatcher()) {
            suspend {
                addMagnetLinkUseCase.invoke(magnetLink)
            }.repeatIf(maxTries = 15) {
                _uiState.value.serverStatus != TorrserverStatus.General.Started
            }?.onSuccess { torrent ->
                suspend { torrentApi.getTorrent(torrent.hash) }
                    .repeatIf(maxTries = 15) { it is Result.Error || it.successResult()?.size == 0L }
                    ?.onSuccess { torrent ->
                        showDetails(torrent.toTorrentUiState())
                    }
            }?.onError { error ->
                _uiState.update { it.copy(error = error.toString()) }
            }
        }
    }
}