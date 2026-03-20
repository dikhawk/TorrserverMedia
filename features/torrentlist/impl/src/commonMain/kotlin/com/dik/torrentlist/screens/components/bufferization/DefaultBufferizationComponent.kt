package com.dik.torrentlist.screens.components.bufferization

import com.arkivanov.decompose.ComponentContext
import com.dik.appsettings.api.model.AppSettings
import com.dik.common.AppDispatchers
import com.dik.common.converter.bytesToBits
import com.dik.common.converter.toReadableSize
import com.dik.common.i18n.LocalizationResource
import com.dik.common.onError
import com.dik.common.onSuccess
import com.dik.common.utils.successResult
import com.dik.themoviedb.SearchTheMovieDbApi
import com.dik.themoviedb.TvEpisodesTheMovieDbApi
import com.dik.themoviedb.model.Movie
import com.dik.themoviedb.model.TvShow
import com.dik.torrentlist.screens.mappers.toTorrentUiState
import com.dik.torrentlist.screens.model.ContentFileUiState
import com.dik.torrentlist.screens.model.TorrentUiState
import com.dik.torrentlist.utils.fileName
import com.dik.torrserverapi.server.api.TorrentApi
import com.dik.videofilenameparser.ParsedBase
import com.dik.videofilenameparser.ParsedShow
import com.dik.videofilenameparser.parseFileNameBase
import com.dik.videofilenameparser.parseFileNameTvShow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import torrservermedia.features.torrentlist.impl.generated.resources.Res
import torrservermedia.features.torrentlist.impl.generated.resources.main_bufferization_season_and_episode


internal class DefaultBufferizationComponent(
    componentContext: ComponentContext,
    private val dispatchers: AppDispatchers,
    private val componentScope: CoroutineScope,
    private val torrentApi: TorrentApi,
    private val searchTheMovieDbApi: SearchTheMovieDbApi,
    private val tvEpisodesTheMovieDbApi: TvEpisodesTheMovieDbApi,
    private val localization: LocalizationResource,
    private val appSettings: AppSettings,
    private val onClickDismiss: () -> Unit,
) : BufferizationComponent, ComponentContext by componentContext {

    private val _uiState = MutableStateFlow(BufferizationState())
    override val uiState = _uiState.asStateFlow()
    private var preloadJob: Job? = null
    private var torrentStatistics: Job? = null

    override fun startBufferization(
        torrent: TorrentUiState,
        contentFile: ContentFileUiState,
        runAfterBufferization: () -> Unit
    ) {
        preloadJob?.cancel()
        resetUiStateTorrentStatistics()
        preloadJob = componentScope.launch {
            showTorrentInfo(contentFile)
            loadOverviewForFile(contentFile)

            torrentApi.preloadTorrent(torrent.hash, contentFile.id).onSuccess {
                torrentStatistics?.cancel()
                runAfterBufferization()
                onClickDismiss()
            }.onError {
                onClickDismiss()
            }
        }

        torrentStatistics(torrent)
    }

    private fun showTorrentInfo(contentFile: ContentFileUiState) {
        _uiState.update {
            it.copy(
                fileName = contentFile.path,
                fileSize = contentFile.length.toReadableSize(),
            )
        }
    }

    private fun torrentStatistics(torrent: TorrentUiState) {
        torrentStatistics?.cancel()
        torrentStatistics = componentScope.launch {
            while (true) {
                torrentApi.getTorrent(torrent.hash)
                    .onSuccess { torrent ->
                        showTorrentStatistics(torrent.toTorrentUiState())
                    }.onError {
                        this.cancel()
                    }

                delay(500)
            }
        }
    }

    private fun showTorrentStatistics(torrent: TorrentUiState) {
        val preloadSize = torrent.statistics?.preloadSize ?: 0L
        val preloadedBytes = torrent.statistics?.preloadedBytes ?: 0L
        val progress = calculateProgress(preloadedBytes, preloadSize)

        _uiState.update {
            it.copy(
                downloadSpeed = torrent.statistics?.downloadSpeed?.bytesToBits() ?: "-",
                downloadProgress = progress,
                downloadProgressText = getProgressMessage(preloadedBytes, preloadSize),
                activePeers = torrent.statistics?.activePeers.toString(),
                totalPeers = torrent.statistics?.totalPeers.toString()
            )
        }
    }

    private fun calculateProgress(preloadedBytes: Long, preloadSize: Long): Float {
        if (preloadSize == 0L) return 0f

        val percent = (preloadedBytes.toFloat() / preloadSize.toFloat())

        return percent
    }

    private fun getProgressMessage(preloadedBytes: Long, preloadSize: Long): String {
        return if (preloadedBytes <= preloadSize) {
            "${preloadedBytes.toReadableSize()}/${preloadSize.toReadableSize()}"
        } else {
            preloadedBytes.toReadableSize()
        }
    }

    override fun onClickCancel() {
        preloadJob?.cancel()
        torrentStatistics?.cancel()
        onClickDismiss()
    }

    private fun resetUiStateTorrentStatistics() {
        _uiState.update { BufferizationState() }
    }

    private fun loadOverviewForFile(contentFile: ContentFileUiState) {
        val fileName = contentFile.path.fileName()
        val tv: ParsedShow? = parseFileNameTvShow(fileName)
        val movie: ParsedBase = parseFileNameBase(fileName)
        val seasonNumber = tv?.seasons?.firstOrNull() ?: 0
        val episodeNumber = tv?.episodeNumbers?.firstOrNull() ?: 0
        val isTv = (seasonNumber > 0) && (episodeNumber > 0)
        val language = appSettings.language.iso

        componentScope.launch {
            val queryTitle = if (isTv) tv?.title ?: "" else movie.title
            val result = searchTheMovieDbApi.multiSearching(query = queryTitle, language = language)
            val content = result.successResult()?.firstOrNull() ?: return@launch

            when (content) {
                is Movie -> overviewForMovie(content)

                is TvShow -> {
                    overviewForTvShow(content, seasonNumber, episodeNumber)
                }
            }
        }
    }

    private suspend fun overviewForTvShow(
        content: TvShow,
        seasonNumber: Int,
        episodeNumber: Int
    ) {
        val language = appSettings.language.iso

        tvEpisodesTheMovieDbApi.details(
            seriesId = content.id,
            seasonNumber = seasonNumber,
            episodeNumber = episodeNumber,
            language = language,
        ).onSuccess { episode ->
            val overview = episode.overview.ifEmpty { content.overview }

            _uiState.update {
                it.copy(
                    overview = overview ?: "",
                    title = content.originalName,
                    titleSecond = prepareTitleSecond(seasonNumber, episodeNumber)
                )
            }
        }
    }

    private fun overviewForMovie(content: Movie) {
        _uiState.update {
            it.copy(
                overview = content.overview,
                title = content.title,
                titleSecond = content.originalTitle
            )
        }
    }

    private suspend fun prepareTitleSecond(seasonNumber: Int, episodeNumber: Int): String {
        return localization.getString(Res.string.main_bufferization_season_and_episode).format(
            seasonNumber, episodeNumber
        )
    }
}