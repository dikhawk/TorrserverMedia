package com.dik.torrentlist.screens.details

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.essenty.lifecycle.doOnResume
import com.dik.appsettings.api.model.AppSettings
import com.dik.common.AppDispatchers
import com.dik.common.utils.successResult
import com.dik.themoviedb.SearchTheMovieDbApi
import com.dik.themoviedb.TvSeasonsTheMovieDbApi
import com.dik.themoviedb.model.Movie
import com.dik.themoviedb.model.TvShow
import com.dik.torrentlist.converters.toReadableSize
import com.dik.torrentlist.di.inject
import com.dik.torrentlist.screens.details.files.DefaultContentFilesComponent
import com.dik.torrentlist.screens.details.torrentstatistics.DefaultTorrentStatisticsComponent
import com.dik.torrentlist.utils.fileName
import com.dik.torrserverapi.ContentFile
import com.dik.torrserverapi.model.Torrent
import com.dik.torrserverapi.server.TorrentApi
import com.dik.videofilenameparser.parseFileNameBase
import com.dik.videofilenameparser.parseFileNameTvShow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import torrservermedia.features.torrentlist.impl.generated.resources.Res
import torrservermedia.features.torrentlist.impl.generated.resources.main_details_season

internal class DefaultDetailsComponent(
    componentContext: ComponentContext,
    private val dispatchers: AppDispatchers = inject(),
    private val torrentApi: TorrentApi = inject(),
    private val appSettings: AppSettings = inject(),
    private val searchingTmdb: SearchTheMovieDbApi = inject(),
    private val tvSeasonTmdb: TvSeasonsTheMovieDbApi = inject(),
    private val onClickPlayFile: suspend (torrent: Torrent, contentFile: ContentFile) -> Unit,
    private val onClickBack: () -> Unit = {}
) : ComponentContext by componentContext, DetailsComponent {

    private val componentScope = CoroutineScope(dispatchers.mainDispatcher() + SupervisorJob())
    private val _uiState = MutableStateFlow(DetailsState())
    override val uiState: StateFlow<DetailsState> = _uiState.asStateFlow()
    private var selectedTorrent: Torrent? = null

    override val contentFilesComponent = DefaultContentFilesComponent(
        componentContext = childContext("content_files"),
        dispatchers = dispatchers,
        appSettings = appSettings,
        componentScope = componentScope,
        onClickPlayFile = {
            val torrent = this.selectedTorrent
            if (torrent != null) onClickPlayFile.invoke(torrent, it)
        }
    )
    override val torrentStatisticsComponent = DefaultTorrentStatisticsComponent(
        componentContext = childContext("torrent_statistics"),
        dispatchers = dispatchers,
        componentScope = componentScope,
        torrrentApi = torrentApi
    )

    override fun onClickBack() = onClickBack.invoke()

    override fun showDetails(hash: String) {
        _uiState.update { DetailsState() }
        componentScope.launch {
            val torrent = torrentApi.getTorrent(hash).successResult() ?: return@launch
            this@DefaultDetailsComponent.selectedTorrent = torrent

            contentFilesComponent.showFiles(torrent.files)
            torrentStatisticsComponent.showStatistics(torrent.hash)
            _uiState.update {
                it.copy(
                    filePath = torrent.title,
                    poster = torrent.poster,
                    size = torrent.size.toReadableSize()
                )
            }
            loadTmdbDetails(torrent)
        }
    }

    private suspend fun loadTmdbDetails(torrent: Torrent) {
        if (torrent.files.isEmpty()) return

        val firstFile = torrent.files.first()
        val fileName = firstFile.path.fileName()
        val parseTvName = parseFileNameTvShow(fileName)
        val parseMovieName = parseFileNameBase(fileName)
        val season = parseTvName?.seasons?.firstOrNull() ?: 0
        val episode = parseTvName?.episodeNumbers?.firstOrNull() ?: 0
        val isTv = (season > 0) && (episode > 0)
        val titleForQuery = if (isTv) parseTvName?.title ?: "" else parseMovieName.title
        val result = searchingTmdb.multiSearching(titleForQuery).successResult()

        if (result.isNullOrEmpty()) return

        when (val content = result.first()) {
            is Movie -> showMovieTmdb(content)
            is TvShow -> showTvShowTmdb(content, season)
        }
    }

    private fun showMovieTmdb(movie: Movie) {
        _uiState.update {
            it.copy(
                title = "${movie.title} (${movie.originalTitle})",
                overview = movie.overview
            )
        }
    }

    private fun showTvShowTmdb(tvShow: TvShow, seasonNumber: Int) {
        val season = if (seasonNumber > 0) seasonNumber.toString() else ""

        componentScope.launch {
            val tvSeason = tvSeasonTmdb.details(tvShow.id, seasonNumber).successResult()
            _uiState.update {
                it.copy(
                    title = "${tvShow.name} (${tvShow.originalName})",
                    seasonNumber = getString(Res.string.main_details_season).format(season),
                    overview = tvSeason?.overview ?: (tvShow.overview ?: "")
                )
            }
        }
    }
}