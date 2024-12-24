package com.dik.torrentlist.screens.components.bufferization

import com.arkivanov.decompose.ComponentContext
import com.dik.common.AppDispatchers
import com.dik.common.Result
import com.dik.common.utils.successResult
import com.dik.themoviedb.SearchTheMovieDbApi
import com.dik.themoviedb.TvEpisodesTheMovieDbApi
import com.dik.themoviedb.model.Movie
import com.dik.themoviedb.model.TvShow
import com.dik.torrentlist.converters.bytesToBits
import com.dik.torrentlist.converters.toReadableSize
import com.dik.torrentlist.utils.fileName
import com.dik.torrserverapi.ContentFile
import com.dik.torrserverapi.model.Torrent
import com.dik.torrserverapi.server.TorrentApi
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
import org.jetbrains.compose.resources.getString
import torrservermedia.features.torrentlist.impl.generated.resources.Res
import torrservermedia.features.torrentlist.impl.generated.resources.main_bufferization_season_and_episode


internal class DefaultBufferizationComponent(
    componentContext: ComponentContext,
    private val dispatchers: AppDispatchers,
    private val componentScope: CoroutineScope,
    private val torrentApi: TorrentApi,
    private val searchTheMovieDbApi: SearchTheMovieDbApi,
    private val tvEpisodesTheMovieDbApi: TvEpisodesTheMovieDbApi,
    private val onClickDismiss: () -> Unit,
) : BufferizationComponent, ComponentContext by componentContext {

    private val _uiState = MutableStateFlow(BufferizationState())
    override val uiState = _uiState.asStateFlow()
    private var preloadJob: Job? = null
    private var torrentStatistics: Job? = null

    override fun startBufferezation(
        torrent: Torrent,
        contentFile: ContentFile,
        runAferBuferazation: () -> Unit
    ) {
        preloadJob?.cancel()
        resetUiStateTorrentStatistics()
        preloadJob = componentScope.launch {
            showTorrentInfo(contentFile)
            loadDescriptionForFile(contentFile)

            val result = torrentApi.preloadTorrent(torrent.hash, contentFile.id)
            when (result) {
                is Result.Success -> {
                    torrentStatistics?.cancel()
                    runAferBuferazation()
                    onClickDismiss()
                }

                is Result.Error -> onClickDismiss()
            }
        }

        torrentStatistics(torrent)
    }

    private fun showTorrentInfo(contentFile: ContentFile) {
        _uiState.update {
            it.copy(
                fileName = contentFile.path,
                fileSize = contentFile.length.toReadableSize(),
            )
        }
    }

    private fun torrentStatistics(torrent: Torrent) {
        torrentStatistics?.cancel()
        torrentStatistics = componentScope.launch {
            while (true) {
                delay(2000)

                val result = torrentApi.getTorrent(torrent.hash)

                when (result) {
                    is Result.Error -> this.cancel()
                    is Result.Success -> showTorrentStatistics(result.data)
                }
            }
        }
    }

    private fun showTorrentStatistics(torrent: Torrent) {
        val preloadSize = torrent.statistics?.preloadSize ?: 0L
        val preloadedBytes = torrent.statistics?.preloadedBytes ?: 0L
        val progress = calculateProgress(preloadedBytes, preloadSize)

        _uiState.update {
            it.copy(
                downloadSpeed = torrent.statistics?.downloadSpeed?.bytesToBits() ?: "-",
                downloadProgress = progress,
                downloadProgressText = getProgressMessage(preloadedBytes, preloadSize)
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

    private fun loadDescriptionForFile(contentFile: ContentFile) {
        val fileName = contentFile.path.fileName()
        val tv: ParsedShow? = parseFileNameTvShow(fileName)
        val movie: ParsedBase = parseFileNameBase(fileName)
        val seasonNumber = tv?.seasons?.firstOrNull() ?: 0
        val episodeNumber = tv?.episodeNumbers?.firstOrNull() ?: 0
        val isTv = (seasonNumber > 0) && (episodeNumber > 0)

        componentScope.launch {
            val queryTitle = if (isTv) tv?.title ?: "" else movie.title
            val result = searchTheMovieDbApi.multiSearching(queryTitle)
            val content = result.successResult()?.firstOrNull()

            when (content) {
                is Movie -> {
                    _uiState.update {
                        it.copy(
                            overview = content.overview,
                            title = content.title,
                            titleSecond = content.originalTitle
                        )
                    }
                }

                is TvShow -> {
                    val episode = tvEpisodesTheMovieDbApi.details(
                        seriesId = content.id,
                        seasonNumber = seasonNumber,
                        episodeNumber = episodeNumber,
                    ).successResult()

                    val overview = if (!episode?.overview.isNullOrEmpty()) episode?.overview else
                        content.overview

                    _uiState.update {
                        it.copy(
                            overview = overview ?: "",
                            title = content.originalName,
                            titleSecond = prepareTitleSecond(seasonNumber, episodeNumber)
                        )
                    }
                }
            }
        }
    }

    private suspend fun prepareTitleSecond(seasonNumber: Int, episodeNumber: Int): String {
        return getString(Res.string.main_bufferization_season_and_episode).format(
            seasonNumber, episodeNumber
        )
    }
}