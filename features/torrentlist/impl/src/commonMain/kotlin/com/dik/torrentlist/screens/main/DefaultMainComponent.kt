package com.dik.torrentlist.screens.main

import androidx.window.core.layout.WindowWidthSizeClass
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.dik.appsettings.api.model.AppSettings
import com.dik.common.AppDispatchers
import com.dik.common.i18n.LocalizationResource
import com.dik.common.platform.WindowAdaptiveClient
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
import com.dik.torrentlist.screens.main.list.DefaultTorrentListComponent
import com.dik.torrentlist.screens.main.list.TorrentListComponent
import com.dik.torrentlist.screens.main.torrserverbar.DefaultTorrserverBarComponent
import com.dik.torrentlist.screens.main.torrserverbar.TorrServerStarterPlatform
import com.dik.torrentlist.screens.main.torrserverbar.TorrserverBarComponent
import com.dik.torrentlist.utils.FileUtils
import com.dik.torrserverapi.ContentFile
import com.dik.torrserverapi.model.Torrent
import com.dik.torrserverapi.model.TorrserverStatus
import com.dik.torrserverapi.server.TorrentApi
import com.dik.torrserverapi.server.TorrserverCommands
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class DefaultMainComponent(
    context: ComponentContext,
    private val torrentApi: TorrentApi = inject(),
    private val dispatchers: AppDispatchers = inject(),
    private val torrserverCommands: TorrserverCommands = inject(),
    private val searchingTmdb: SearchTheMovieDbApi = inject(),
    private val addTorrentFile: AddTorrentFile = inject(),
    private val addMagnetLink: AddMagnetLink = inject(),
    private val tvEpisodesTmdb: TvEpisodesTheMovieDbApi = inject(),
    private val windowAdaptiveClient: WindowAdaptiveClient = inject(),
    private val tvSeasonTmdb: TvSeasonsTheMovieDbApi = inject(),
    private val appSettings: AppSettings = inject(),
    private val localization: LocalizationResource = inject(),
    private val torrServerStarter: TorrServerStarterPlatform = inject(),
    private val findPosterForTorrent: FindPosterForTorrent = inject(),
    private val fileUtils: FileUtils = inject(),
    private val openSettingsScreen: () -> Unit = {},
    private val onClickPlayFile: suspend (contentFile: ContentFile) -> Unit,
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
        torrserverCommands = torrserverCommands,
        addTorrentFile = addTorrentFile,
        addMagnetLink = addMagnetLink,
        localization = localization,
        fileUtils = fileUtils
    )

    override val torrserverBarComponent: TorrserverBarComponent =
        DefaultTorrserverBarComponent(
            context = childContext("torrserver_bar"),
            torrserverCommands = torrserverCommands,
            dispatchers = dispatchers,
            componentScope = componentScope,
            localization = localization,
            torrServerStarter = torrServerStarter
        )

    override val torrentListComponent: TorrentListComponent = DefaultTorrentListComponent(
        context = childContext("torrserverbar"),
        onTorrentClick = { torrent -> showDetails(torrent) },
        onTorrentsIsEmpty = { isEmpty ->
            if (isEmpty) _uiState.update { it.copy(isShowDetails = false) }
        },
        torrentApi = torrentApi,
        componentScope = componentScope,
        addTorrentFile = addTorrentFile,
        fileUtils = fileUtils
    )

    private fun showDetails(torrent: Torrent) {
        val windowAdaptiveFlow = windowAdaptiveClient.windowAdaptiveFlow()
        val windowWidthSizeClass = windowAdaptiveFlow.value
            ?.windowSizeClass?.windowWidthSizeClass ?: return

        when (windowWidthSizeClass) {
            WindowWidthSizeClass.COMPACT -> {
                navigateToDetails(torrent.hash, torrent.poster)
            }

            else -> {
                detailsComponent.showDetails(torrent.hash)
                _uiState.update { it.copy(isShowDetails = true) }
            }
        }
    }

    private fun playFile(contentFile: ContentFile) {
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
            findPosterForTorrent = findPosterForTorrent,
            screenFormat = DetailsComponentScreenFormat.PANE,
            onClickPlayFile = { torrent, contentFile ->
                _uiState.update { it.copy(isShowDetails = true, isShowBufferization = true) }
                bufferizationComponent.startBufferezation(
                    torrent = torrent,
                    contentFile = contentFile,
                    runAferBuferazation = { playFile(contentFile) }
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
            torrserverCommands.serverStatus().collect { status ->
                _uiState.update { it.copy(serverStatus = status) }
            }
        }
    }

    fun addTorrentAndShowDetails(pathToTorrent: String) {
        componentScope.launch {
            var tries = 0
            val maxTries = 100
            val delay = 100L

            while (tries < maxTries) {
                tries++

                if (_uiState.value.serverStatus == TorrserverStatus.STARTED) {
                    val result = addTorrentFile.invoke(pathToTorrent)
                    if (result.torrent != null) {
                        showDetails(result.torrent)
                    }

                    break
                }

                delay(delay)
            }
        }
    }
}