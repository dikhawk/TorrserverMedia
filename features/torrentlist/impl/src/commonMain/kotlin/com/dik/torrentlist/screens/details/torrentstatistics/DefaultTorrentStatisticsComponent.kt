package com.dik.torrentlist.screens.details.torrentstatistics

import com.arkivanov.decompose.ComponentContext
import com.dik.common.AppDispatchers
import com.dik.common.Result
import com.dik.common.converter.bytesToBits
import com.dik.common.converter.toReadableSize
import com.dik.common.i18n.LocalizationResource
import com.dik.torrentlist.error.toMessage
import com.dik.torrserverapi.TorrserverError
import com.dik.torrserverapi.model.Torrent
import com.dik.torrserverapi.server.api.TorrentApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val STATISTICS_REFRESH_DELAY = 2_000L

class DefaultTorrentStatisticsComponent(
    componentContext: ComponentContext,
    private val dispatchers: AppDispatchers,
    private val componentScope: CoroutineScope,
    private val torrentApi: TorrentApi,
    private val localization: LocalizationResource
) : TorrentStatisticsComponent, ComponentContext by componentContext {

    private val _uiState = MutableStateFlow(TorrentStatisticsState())
    override val uiState: StateFlow<TorrentStatisticsState> = _uiState.asStateFlow()
    private var showStatisticsJob: Job? = null


    override fun showStatistics(hash: String) {
        showStatisticsJob?.cancel()

        showStatisticsJob = componentScope.launch(dispatchers.defaultDispatcher()) {
            while (true) {
                when (val result = torrentApi.getTorrent(hash)) {
                    is Result.Error -> showError(result.error)
                    is Result.Success -> updateUiState(result.data)
                }

                delay(STATISTICS_REFRESH_DELAY)
            }
        }
    }

    private fun updateUiState(torrent: Torrent) {
        val statistics = torrent.statistics ?: return

        _uiState.update {
            it.copy(
                torrentStatus = statistics.torrentStatus,
                loadedSize = statistics.loadedSize.toReadableSize(),
                torrentSize = torrent.size.toReadableSize(),
                preloadedBytes = statistics.preloadedBytes.toReadableSize(),
                downloadSpeed = statistics.downloadSpeed.bytesToBits(),
                uploadSpeed = statistics.uploadSpeed.bytesToBits(),
                totalPeers = statistics.totalPeers.toString(),
                activePeers = statistics.activePeers.toString(),
            )
        }
    }

    private suspend fun showError(error: TorrserverError) {
        val errorMessage = error.toMessage(localization)
        _uiState.update { it.copy(error = errorMessage) }
    }
}