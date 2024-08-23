package com.dik.torrentlist.screens.details

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.dik.appsettings.api.model.AppSettings
import com.dik.common.AppDispatchers
import com.dik.common.cmd.CmdRunner
import com.dik.common.utils.successResult
import com.dik.torrentlist.converters.toReadableSize
import com.dik.torrentlist.di.inject
import com.dik.torrentlist.screens.details.files.DefaultContentFilesComponent
import com.dik.torrentlist.screens.details.torrentstatistics.DefaultTorrentStatisticsComponent
import com.dik.torrserverapi.model.Torrent
import com.dik.torrserverapi.server.TorrentApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class DefaultDetailsComponent(
    componentContext: ComponentContext,
    private val dispatchers: AppDispatchers = inject(),
    private val cmdRunner: CmdRunner = inject(),
    private val torrrentApi: TorrentApi = inject(),
    private val appSettings: AppSettings = inject(),
) : ComponentContext by componentContext, DetailsComponent {

    private val componentScope = CoroutineScope(dispatchers.mainDispatcher() + SupervisorJob())
    private val _uiState = MutableStateFlow<DetailsState>(DetailsState())
    override val uiState: StateFlow<DetailsState> = _uiState.asStateFlow()


    override val contentFilesComponent = DefaultContentFilesComponent(
        componentContext = childContext("content_files"),
        dispatchers = dispatchers,
        cmdRunner = cmdRunner,
        appSettings = appSettings
    )
    override val torrentStatisticsComponent = DefaultTorrentStatisticsComponent(
        componentContext = childContext("torrent_statistics"),
        dispatchers = dispatchers,
        torrrentApi = torrrentApi
    )

    override fun showDetails(hash: String) {
        componentScope.launch {
            val torrent = torrrentApi.getTorrent(hash).successResult() ?: return@launch

            contentFilesComponent.showFiles(torrent.files)
            torrentStatisticsComponent.showStatistics(torrent.hash)
            _uiState.update {
                it.copy(
                    title = torrent.title,
                    poster = torrent.poster,
                    size = torrent.size.toReadableSize()
                )
            }
        }
    }
}