package com.dik.torrentlist.screens.details.torrentstatistics

import com.arkivanov.decompose.ComponentContext
import com.dik.common.AppDispatchers
import com.dik.common.Result
import com.dik.common.i18n.LocalizationResource
import com.dik.torrentlist.converters.bytesToBits
import com.dik.torrentlist.converters.toReadableSize
import com.dik.torrentlist.error.toMessage
import com.dik.torrserverapi.TorrserverError
import com.dik.torrserverapi.model.Torrent
import com.dik.torrserverapi.server.TorrentApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DefaultTorrentStatisticsComponent(
    componentContext: ComponentContext,
    private val dispatchers: AppDispatchers,
    private val componentScope: CoroutineScope,
    private val torrrentApi: TorrentApi,
    private val localization: LocalizationResource
) : TorrentStatisticsComponent, ComponentContext by componentContext {

    private val _uiState = MutableStateFlow(TorrentStatisticsState())
    override val uiState: StateFlow<TorrentStatisticsState> = _uiState.asStateFlow()
    private var showStatisticsJob: Job? = null


    override fun showStatistics(hash: String) {
        showStatisticsJob?.cancel()

        showStatisticsJob = componentScope.launch(dispatchers.defaultDispatcher()) {
            while (true) {
                val result = torrrentApi.getTorrent(hash)
                when (val res = result) {
                    is Result.Error -> showError(res.error)
                    is Result.Success -> updateUiState(res.data)
                }

                delay(2000)
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

    private fun showError(error: TorrserverError) {
        componentScope.launch {
            _uiState.update { it.copy(error = error.toMessage(localization)) }
        }
    }
}