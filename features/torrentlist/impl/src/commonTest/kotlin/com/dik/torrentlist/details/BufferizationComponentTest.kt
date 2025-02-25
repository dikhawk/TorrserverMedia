package com.dik.torrentlist.details

import app.cash.turbine.test
import com.dik.common.AppDispatchers
import com.dik.common.Result
import com.dik.common.i18n.LocalizationResource
import com.dik.themoviedb.SearchTheMovieDbApi
import com.dik.themoviedb.TvEpisodesTheMovieDbApi
import com.dik.themoviedb.errors.TheMovieDbError
import com.dik.themoviedb.model.Movie
import com.dik.themoviedb.model.TvEpisode
import com.dik.themoviedb.model.TvShow
import com.dik.torrentlist.converters.bytesToBits
import com.dik.torrentlist.converters.toReadableSize
import com.dik.torrentlist.screens.components.bufferization.DefaultBufferizationComponent
import com.dik.torrentlist.utils.fileName
import com.dik.torrserverapi.ContentFile
import com.dik.torrserverapi.model.PlayStatistics
import com.dik.torrserverapi.model.Torrent
import com.dik.torrserverapi.server.TorrentApi
import com.dik.videofilenameparser.parseFileNameBase
import com.dik.videofilenameparser.parseFileNameTvShow
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import torrservermedia.features.torrentlist.impl.generated.resources.Res
import torrservermedia.features.torrentlist.impl.generated.resources.main_bufferization_season_and_episode
import kotlin.test.Test
import kotlin.test.assertEquals

class BufferizationComponentTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    private val unconfiedTestDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(unconfiedTestDispatcher)
    private val dispatchers: AppDispatchers = object : AppDispatchers {
        override fun ioDispatcher() = unconfiedTestDispatcher
        override fun defaultDispatcher() = unconfiedTestDispatcher
        override fun mainDispatcher() = unconfiedTestDispatcher
    }
    private val torrentApi: TorrentApi = mockk()
    private val searchTheMovieDbApi: SearchTheMovieDbApi = mockk()
    private val tvEpisodesTheMovieDbApi: TvEpisodesTheMovieDbApi = mockk()
    private val localization: LocalizationResource = mockk()
    private val onClickDismiss: () -> Unit = mockk(relaxed = true)

    
    @Test
    fun `Start bufferization and check show torrent info`() = runTest {
        val torrent = Torrent(
            hash = this.hashCode().toString(),
            title = "At the edge of the abyss",
            poster = "poster",
            name = "name",
            size = 123456789L,
            files = listOf(),
        )
        val contentFile = ContentFile(
            id = this.hashCode(),
            path = "Season 1/At the edge of the abyss.S1E1.1080p.rus.mkv",
            length = 123456789L,
            url = "url_to",
            isViewed = false
        )
        val component = bufferizationComponentTest()

        coEvery { torrentApi.preloadTorrent(torrent.hash, contentFile.id) } returns Result.Success(Unit)

        component.startBufferezation(torrent, contentFile, {})

        component.uiState.test {
            val state = awaitItem()

            assertEquals(contentFile.path, state.fileName)
            assertEquals(contentFile.length.toReadableSize(), state.fileSize)
        }
    }

    @Test
    fun `Start bufferization and check show overview for movie`() = runTest {
        val torrent = Torrent(
            hash = this.hashCode().toString(),
            title = "At the edge of the abyss",
            poster = "poster",
            name = "name",
            size = 123456789L,
            files = listOf(),
        )
        val contentFile = ContentFile(
            id = this.hashCode(),
            path = "Season 1/At the edge of the abyss.1080p.rus.mkv",
            length = 123456789L,
            url = "url_to",
            isViewed = false
        )
        val movie: Movie = mockk(relaxed = true) {
            every { overview } returns "About movie"
            every { title } returns "At the edge of the abyss"
            every { originalTitle } returns "У края бездны"
        }
        val scruppedMovieTitle = parseFileNameBase(torrent.title).title
        val component = bufferizationComponentTest()

        coEvery { torrentApi.preloadTorrent(torrent.hash, contentFile.id) } returns Result.Success(Unit)
        coEvery { searchTheMovieDbApi.multiSearching(scruppedMovieTitle) } returns Result.Success(
            listOf(movie)
        )

        component.startBufferezation(torrent, contentFile, {})

        component.uiState.test {
            val state = awaitItem()

            assertEquals(movie.overview, state.overview)
            assertEquals(movie.title, state.title)
            assertEquals(movie.originalTitle, state.titleSecond)
        }
    }

    @Test
    fun `Start bufferization and check show overview for tv show`() = runTest {
        val torrent = Torrent(
            hash = this.hashCode().toString(),
            title = "At the edge of the abyss",
            poster = "poster",
            name = "name",
            size = 123456789L,
            files = listOf(),
        )
        val contentFile = ContentFile(
            id = this.hashCode(),
            path = "Season 1/At the edge of the abyss.S1E1.1080p.rus.mkv",
            length = 123456789L,
            url = "url_to",
            isViewed = false
        )
        val tvShow: TvShow = mockk(relaxed = true) {
            every { overview } returns "About movie"
            every { originalName } returns "At the edge of the abyss"
        }
        val scruppedTvShow = parseFileNameTvShow(contentFile.path.fileName())
        val season = scruppedTvShow!!.seasons.first()
        val episode = scruppedTvShow.episodeNumbers.first()
        val tvShowTitleMask = "TvShow: %s, %s"
        val component = bufferizationComponentTest()
        val tvEpisode: TvEpisode = mockk(relaxed = true) { every { overview } returns "About tv show" }

        coEvery { torrentApi.preloadTorrent(torrent.hash, contentFile.id) } returns Result.Success(Unit)
        coEvery { searchTheMovieDbApi.multiSearching(scruppedTvShow.title) } returns
                Result.Success(listOf(tvShow))
        coEvery { localization.getString(Res.string.main_bufferization_season_and_episode) } returns tvShowTitleMask
        coEvery { tvEpisodesTheMovieDbApi.details(
            seriesId = tvShow.id, seasonNumber = season, episodeNumber = episode
        ) } returns Result.Success(tvEpisode)

        component.startBufferezation(torrent, contentFile, {})

        component.uiState.test {
            val state = awaitItem()

            assertEquals(tvEpisode.overview, state.overview)
            assertEquals(tvShow.originalName, state.title)
            assertEquals(tvShowTitleMask.format(season, episode), state.titleSecond)
        }
    }

    @Test
    fun `Start bufferization where preloadTorrent is success then check runAferBuferazation and disssmiss`() = runTest {
        val torrent = Torrent(
            hash = this.hashCode().toString(),
            title = "At the edge of the abyss",
            poster = "poster",
            name = "name",
            size = 123456789L,
            files = listOf(),
        )
        val contentFile = ContentFile(
            id = this.hashCode(),
            path = "Season 1/At the edge of the abyss.S1E1.1080p.rus.mkv",
            length = 123456789L,
            url = "url_to",
            isViewed = false
        )
        val component = bufferizationComponentTest()
        val runAferBuferazation: () -> Unit = mockk(relaxed = true)

        coEvery { torrentApi.preloadTorrent(torrent.hash, contentFile.id) } returns Result.Success(Unit)
        coEvery { searchTheMovieDbApi.multiSearching(any()) } returns Result.Success(
            listOf(mockk<Movie>(relaxed = true))
        )

        component.startBufferezation(torrent, contentFile, runAferBuferazation)

        verify(exactly = 1) { runAferBuferazation.invoke() }
        verify(exactly = 1) { onClickDismiss.invoke() }
    }

    @Test
    fun `Start bufferization where preloadTorrent is failure then check disssmiss`() = runTest {
        val torrent = Torrent(
            hash = this.hashCode().toString(),
            title = "At the edge of the abyss",
            poster = "poster",
            name = "name",
            size = 123456789L,
            files = listOf(),
        )
        val contentFile = ContentFile(
            id = this.hashCode(),
            path = "Season 1/At the edge of the abyss.S1E1.1080p.rus.mkv",
            length = 123456789L,
            url = "url_to",
            isViewed = false
        )
        val component = bufferizationComponentTest()
        val runAferBuferazation: () -> Unit = mockk(relaxed = true)

        coEvery { torrentApi.preloadTorrent(torrent.hash, contentFile.id) } returns Result.Success(Unit)
        coEvery { searchTheMovieDbApi.multiSearching(any()) } returns Result.Error(
            TheMovieDbError.HttpError.ResponseReturnNull
        )

        component.startBufferezation(torrent, contentFile, runAferBuferazation)

        verify(exactly = 1) { onClickDismiss.invoke() }
    }

    @Test
    fun `Start bufferization and show torrent statistics`() = runTest {
        val statistics: PlayStatistics = mockk(relaxed = true) {
            every { torrentStatus } returns "Ready"
            every { loadedSize } returns 1024
            every { preloadedBytes } returns 128000
            every { preloadSize } returns 64000
            every { downloadSpeed } returns 128000.0
            every { uploadSpeed } returns 64000.0
            every { totalPeers } returns 25
            every { activePeers } returns 10
        }
        val torrent = Torrent(
            hash = this.hashCode().toString(),
            title = "At the edge of the abyss",
            poster = "poster",
            name = "name",
            size = 123456789L,
            files = listOf(),
            statistics = statistics
        )
        val contentFile = ContentFile(
            id = this.hashCode(),
            path = "Season 1/At the edge of the abyss.S1E1.1080p.rus.mkv",
            length = 123456789L,
            url = "url_to",
            isViewed = false
        )
        val component = bufferizationComponentTest()
        val runAferBuferazation: () -> Unit = mockk(relaxed = true)

        coEvery { torrentApi.preloadTorrent(torrent.hash, contentFile.id) } returns Result.Success(Unit)
        coEvery { searchTheMovieDbApi.multiSearching(any()) } returns Result.Success(
            listOf(mockk<Movie>(relaxed = true))
        )
        coEvery { torrentApi.getTorrent(torrent.hash) } returns Result.Success(torrent)

        component.startBufferezation(torrent, contentFile, runAferBuferazation)
        testScope.cancel()

        component.uiState.test {
            val state = awaitItem()
            val progress = (statistics.preloadedBytes.toFloat() / statistics.preloadSize.toFloat())
            val downloadProgress = if (statistics.preloadedBytes <= statistics.preloadSize) {
                "${statistics.preloadedBytes.toReadableSize()}/${statistics.preloadSize.toReadableSize()}"
            } else {
                statistics.preloadedBytes.toReadableSize()
            }

            assertEquals(statistics.downloadSpeed.bytesToBits(), state.downloadSpeed)
            assertEquals(progress, state.downloadProgress)
            assertEquals(downloadProgress, state.downloadProgressText)
            assertEquals(statistics.activePeers.toString(), state.activePeers)
            assertEquals(statistics.totalPeers.toString(), state.totalPeers)
        }
    }

    private fun bufferizationComponentTest() = DefaultBufferizationComponent(
        componentContext = mockk(relaxed = true),
        dispatchers = dispatchers,
        componentScope = testScope,
        torrentApi = torrentApi,
        searchTheMovieDbApi = searchTheMovieDbApi,
        tvEpisodesTheMovieDbApi = tvEpisodesTheMovieDbApi,
        localization = localization,
        onClickDismiss = onClickDismiss
    )
}