package com.dik.torrentlist.details

import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.dik.appsettings.api.model.AppSettings
import com.dik.common.AppDispatchers
import com.dik.common.Result
import com.dik.common.i18n.AppLanguage
import com.dik.common.i18n.LocalizationResource
import com.dik.themoviedb.SearchTheMovieDbApi
import com.dik.themoviedb.TvEpisodesTheMovieDbApi
import com.dik.themoviedb.TvSeasonsTheMovieDbApi
import com.dik.themoviedb.model.Movie
import com.dik.themoviedb.model.TvSeason
import com.dik.themoviedb.model.TvShow
import com.dik.torrentlist.converters.toReadableSize
import com.dik.torrentlist.screens.details.DefaultDetailsComponent
import com.dik.torrentlist.screens.details.DetailsComponentScreenFormat
import com.dik.torrentlist.screens.main.FindPosterForTorrent
import com.dik.torrentlist.screens.main.Poster
import com.dik.torrentlist.utils.fileName
import com.dik.torrserverapi.ContentFile
import com.dik.torrserverapi.model.Torrent
import com.dik.torrserverapi.server.TorrentApi
import com.dik.videofilenameparser.parseFileNameBase
import com.dik.videofilenameparser.parseFileNameTvShow
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import torrservermedia.features.torrentlist.impl.generated.resources.Res
import torrservermedia.features.torrentlist.impl.generated.resources.main_details_season
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DefaultDetailsComponentTest {

    private val lifecycle = LifecycleRegistry()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val unconfiedDispatcher = UnconfinedTestDispatcher()
    private val dispatchers: AppDispatchers = object : AppDispatchers {
        override fun ioDispatcher() = unconfiedDispatcher
        override fun defaultDispatcher() = unconfiedDispatcher
        override fun mainDispatcher() = unconfiedDispatcher
    }
    private val testScope = TestScope(unconfiedDispatcher)
    private val torrentApi: TorrentApi = mockk()
    private val appSettings: AppSettings = mockk(relaxed = true) {
        every { language } returns AppLanguage.RUSSIAN
    }
    private val searchingTmdb: SearchTheMovieDbApi = mockk()
    private val tvSeasonTmdb: TvSeasonsTheMovieDbApi = mockk()
    private val tvEpisodesTmdb: TvEpisodesTheMovieDbApi = mockk()
    private val screenFormat: DetailsComponentScreenFormat = mockk()
    private val localization: LocalizationResource = mockk(relaxed = true)
    private val findPosterForTorrent: FindPosterForTorrent = mockk(relaxed = true)
    private val onClickPlayFile: suspend (torrent: Torrent, contentFile: ContentFile) -> Unit =
        mockk()
    private val onClickBack: () -> Unit = {}


    @Test
    fun `showDetails should update uiState with torrent details`() = runTest {
        val torrent = Torrent(
            hash = this.hashCode().toString(),
            title = "title",
            poster = "poster",
            name = "name",
            size = 123456789L,
            files = emptyList(),
        )
        val hash = torrent.hash
        val component = detailsComponent()

        coEvery { torrentApi.getTorrent(hash) } returns Result.Success(torrent)

        component.showDetails(hash)

        component.uiState.test {
            val state = awaitItem()

            assertEquals(torrent.title, state.torrentName)
            assertEquals(torrent.poster, state.poster)
            assertEquals(torrent.size.toReadableSize(), state.size)
        }
    }

    @Test
    fun `loadTmdbDetails load details for Movie then check ui state`() = runTest {
        val contentFile = ContentFile(
            id = this.hashCode(),
            path = "At the edge of the abyss.1080p.rus.mkv",
            length = 123456789L,
            url = "url_to",
            isViewed = false
        )
        val torrent = Torrent(
            hash = this.hashCode().toString(),
            title = "At the edge of the abyss",
            poster = "poster",
            name = "name",
            size = 123456789L,
            files = listOf(contentFile),
        )
        val hash = torrent.hash
        val component = detailsComponent()
        val movie = mockk<Movie>(relaxed = true) {
            every { title } returns "At the edge of the abyss"
            every { originalTitle } returns "У края бездны"
            every { poster500 } returns "poster500.jpg"
            every { overview } returns "About the movie"
        }
        val query = parseFileNameBase(torrent.title).title

        coEvery { torrentApi.getTorrent(hash) } returns Result.Success(torrent)
        coEvery {
            searchingTmdb.multiSearching(query = query, language = AppLanguage.RUSSIAN.iso)
        } returns Result.Success(listOf(movie))

        component.showDetails(hash)

        component.uiState.test {
            val state = awaitItem()
            assertEquals("${movie.title} (${movie.originalTitle})", state.title)
            assertEquals(movie.overview, state.overview)
            assertEquals(movie.poster500, state.poster)
        }
    }

    @Test
    fun `loadTmdbDetails load details for TvShow then check ui state`() = runTest {
        val contentFile = ContentFile(
            id = this.hashCode(),
            path = "At the edge of the abyss.S01E04.1080p.rus.mkv",
            length = 123456789L,
            url = "url_to",
            isViewed = false
        )
        val torrent = Torrent(
            hash = this.hashCode().toString(),
            title = "At the edge of the abyss",
            poster = "poster",
            name = "name",
            size = 123456789L,
            files = listOf(contentFile),
        )
        val hash = torrent.hash
        val component = detailsComponent()
        val tvShow = mockk<TvShow>(relaxed = true) {
            every { name } returns "At the edge of the abyss"
            every { originalName } returns "У края бездны"
            every { poster500 } returns "poster500.jpg"
            every { overview } returns "About the tv show"
        }
        val tvSeason = mockk<TvSeason>(relaxed = true) {
            every { overview } returns "About the tv show"
        }
        val parseTvShow = parseFileNameTvShow(contentFile.path.fileName())
        val seasonNumber = parseTvShow?.seasons!!.firstOrNull().toString()
        val seasonNumberMask = "Season: %s"

        coEvery { localization.getString(Res.string.main_details_season) } returns seasonNumberMask
        coEvery { torrentApi.getTorrent(hash) } returns Result.Success(torrent)
        coEvery {
            searchingTmdb.multiSearching(
                query = parseTvShow.title,
                language = AppLanguage.RUSSIAN.iso
            )
        } returns Result.Success(listOf(tvShow))
        coEvery {
            tvSeasonTmdb.details(
                tvShow.id,
                parseTvShow.seasons.first()
            )
        } returns Result.Success(tvSeason)

        component.showDetails(hash)

        component.uiState.test {
            val state = awaitItem()
            assertEquals("${tvShow.name} (${tvShow.originalName})", state.title)
            assertEquals(tvShow.overview, state.overview)
            assertEquals(seasonNumberMask.format(seasonNumber), state.seasonNumber)
        }
    }

    @Test
    fun `Run bufferezation then check buffrezation is show`() = runTest {
        val component = detailsComponent()
        val contentFile = ContentFile(
            id = this.hashCode(),
            path = "At the edge of the abyss.S01E04.1080p.rus.mkv",
            length = 123456789L,
            url = "url_to",
            isViewed = false
        )
        val torrent = Torrent(
            hash = this.hashCode().toString(),
            title = "At the edge of the abyss",
            poster = "poster",
            name = "name",
            size = 123456789L,
            files = listOf(contentFile),
        )

        coEvery { torrentApi.preloadTorrent(any(), any()) } returns Result.Success(Unit)
        coEvery {
            searchingTmdb.multiSearching(query = any(), language = AppLanguage.RUSSIAN.iso)
        } returns Result.Success(emptyList())
        coEvery { torrentApi.getTorrent(torrent.hash) } returns Result.Success(torrent)

        component.runBufferization(
            torrent = torrent,
            contentFile = contentFile,
            runAferBuferazation = {})

        component.uiState.test {
            val state = awaitItem()
            assertTrue(state.isShowBufferization)
        }
    }

    @Test
    fun `Click delete torrent then check removeTorrent is called`() = runTest {
        val torrent = Torrent(
            hash = this.hashCode().toString(),
            title = "At the edge of the abyss",
            poster = "poster",
            name = "name",
            size = 123456789L,
            files = listOf(),
        )
        val component = detailsComponent()

        coEvery { torrentApi.getTorrent(torrent.hash) } returns Result.Success(torrent)
        coEvery { torrentApi.removeTorrent(torrent.hash) } returns Result.Success(Unit)

        component.showDetails(torrent.hash)
        component.onClickDeleteTorrent()

        coVerify { torrentApi.removeTorrent(torrent.hash) }
    }

    @Test
    fun `If poster in torrent is empty Then find poster`() = runTest {
        val torrent = Torrent(
            hash = this.hashCode().toString(),
            title = "At the edge of the abyss",
            poster = "",
            name = "name",
            size = 123456789L,
            files = listOf(),
        )
        val component = detailsComponent()

        coEvery { torrentApi.getTorrent(torrent.hash) } returns Result.Success(torrent)
        coEvery { findPosterForTorrent.invoke(torrent) } returns
                Result.Success(
                    Poster(
                        poster300 = "poster300.jpg",
                        poster500 = "poster500.jpg",
                        posterOriginal = "posterOriginal.jpg"
                    )
                )
        coEvery { torrentApi.updateTorrent(any()) } returns Result.Success(Unit)

        component.showDetails(torrent.hash)

        coVerify { torrentApi.updateTorrent(match { it.poster == "poster300.jpg" }) }
    }

    private fun detailsComponent() = DefaultDetailsComponent(
        componentContext = DefaultComponentContext(lifecycle = lifecycle),
        dispatchers = dispatchers,
        torrentApi = torrentApi,
        appSettings = appSettings,
        searchingTmdb = searchingTmdb,
        tvSeasonTmdb = tvSeasonTmdb,
        tvEpisodesTmdb = tvEpisodesTmdb,
        screenFormat = screenFormat,
        localization = localization,
        findPosterForTorrent = findPosterForTorrent,
        onClickPlayFile = onClickPlayFile,
        onClickBack = onClickBack
    )
}