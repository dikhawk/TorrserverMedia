package com.dik.torrentlist.main

import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.destroy
import com.dik.appsettings.api.model.AppSettings
import com.dik.common.AppDispatchers
import com.dik.common.Result
import com.dik.common.i18n.AppLanguage
import com.dik.common.i18n.LocalizationResource
import com.dik.themoviedb.SearchTheMovieDbApi
import com.dik.themoviedb.TvEpisodesTheMovieDbApi
import com.dik.themoviedb.TvSeasonsTheMovieDbApi
import com.dik.themoviedb.model.Movie
import com.dik.torrentlist.domain.ServerStatus
import com.dik.torrentlist.screens.main.DefaultMainComponent
import com.dik.torrentlist.screens.main.domain.AddMagnetLinkUseCase
import com.dik.torrentlist.screens.main.domain.AddTorrentFileUseCase
import com.dik.torrentlist.screens.main.domain.FindPosterUseCase
import com.dik.torrentlist.utils.FileUtils
import com.dik.torrserverapi.TorrserverError
import com.dik.torrserverapi.model.Torrent
import com.dik.torrserverapi.server.TorrserverManager
import com.dik.torrserverapi.server.TorrserverStatus
import com.dik.torrserverapi.server.api.TorrentApi
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DefaultMainComponentTest {

    private val lifecycle = LifecycleRegistry()
    private val torrentApi: TorrentApi = mockk()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val unconfiedTestDispatcher = UnconfinedTestDispatcher()
    private val dispatchers = object : AppDispatchers {
        override fun ioDispatcher() = unconfiedTestDispatcher
        override fun defaultDispatcher() = unconfiedTestDispatcher
        override fun mainDispatcher() = unconfiedTestDispatcher
    }
    private val torrServerStatusFlow = MutableSharedFlow<TorrserverStatus>(replay = 1)
    private val torrserverManager: TorrserverManager = mockk {
        coEvery { observeTorrserverStatus() } returns torrServerStatusFlow.asSharedFlow()
    }
    private val searchingTmdb: SearchTheMovieDbApi = mockk()
    private val addTorrentFileUseCase: AddTorrentFileUseCase = mockk()
    private val addMagnetLinkUseCase: AddMagnetLinkUseCase = mockk()
    private val tvEpisodesTmdb: TvEpisodesTheMovieDbApi = mockk()
    private val appSettings: AppSettings = mockk(relaxed = true) {
        every { language } returns AppLanguage.RUSSIAN
    }
    private val findPosterUseCase: FindPosterUseCase = mockk(relaxed = true)
    private val tvSeasonTmdb: TvSeasonsTheMovieDbApi = mockk()
    private val navigateToDetails: (torrentHash: String, poster: String) -> Unit =
        mockk(relaxed = true)
    private val localization: LocalizationResource = mockk()
    private val fileUtils: FileUtils = mockk()


    @Test
    fun `On click item for not COMPACT screen then check isShowDetails is true`() = runTest {
        val torretnsResult: Result<List<Torrent>, TorrserverError> =
            Result.Success(listOf(mockk(relaxed = true)))
        coEvery { torrentApi.getTorrents() } returns torretnsResult
        coEvery { torrentApi.observeTorrents() } returns flowOf(torretnsResult)
        coEvery { torrentApi.getTorrent(any()) } returns Result.Success(mockk(relaxed = true))
        every { torrserverManager.observeTorrserverStatus() } returns flowOf(TorrserverStatus.General.Started)

        val defaultMainComponent = getDefaultMainComponent()

        defaultMainComponent.uiState.test {
            skipItems(1)
            defaultMainComponent.torrentListComponent.onClickItem(mockk(relaxed = true))
            assertTrue(defaultMainComponent.uiState.value.isShowDetails)
            skipItems(1)
        }
    }

    @Test
    fun `On click item for COMPACT screen then check navigate to details screen`() = runTest {
        val torretnsResult: Result<List<Torrent>, TorrserverError> =
            Result.Success(listOf(mockk(relaxed = true)))
        coEvery { torrentApi.observeTorrents() } returns flowOf(torretnsResult)
        coEvery { torrentApi.getTorrents() } returns torretnsResult
        coEvery { torrentApi.getTorrent(any()) } returns Result.Success(mockk(relaxed = true))
        every { torrserverManager.observeTorrserverStatus() } returns flowOf(TorrserverStatus.General.Started)

        val defaultMainComponent = getDefaultMainComponent()
        defaultMainComponent.torrentListComponent.onNavigateToDetails(mockk(relaxed = true))

        verify(exactly = 1) { navigateToDetails(any(), any()) }
    }

    @Test
    fun `Change server status then check ui state serverStatus after change`() = runTest {
        val torretnsResult: Result<List<Torrent>, TorrserverError> = Result.Success(emptyList())
        val torrserverStatus = MutableStateFlow<TorrserverStatus>(TorrserverStatus.Unknown("Init state"))
        coEvery { torrentApi.observeTorrents() } returns flowOf(torretnsResult)
        coEvery { torrentApi.getTorrents() } returns torretnsResult
        every { torrserverManager.observeTorrserverStatus() } returns torrserverStatus

        val defaultMainComponent = getDefaultMainComponent()

        defaultMainComponent.uiState.test {
            assertEquals(ServerStatus.Unknown("Init state"), awaitItem().serverStatus)
            torrserverStatus.emit(TorrserverStatus.General.Running)
            assertEquals(ServerStatus.General.Running, awaitItem().serverStatus)
            lifecycle.destroy()
        }
    }

    @Test
    fun `On click play file then check bufferization is visible`() = runTest {
        val torretnsResult: Result<List<Torrent>, TorrserverError> = Result.Success(emptyList())
        coEvery { torrentApi.observeTorrents() } returns flowOf(torretnsResult)
        coEvery { torrentApi.getTorrents() } returns torretnsResult
        coEvery { torrentApi.getTorrent(any()) } returns Result.Success(mockk(relaxed = true))
        coEvery {
            searchingTmdb.multiSearching(query = any(), language = AppLanguage.RUSSIAN.iso)
        } returns Result.Success(listOf(mockk<Movie>(relaxed = true)))
        coEvery { torrentApi.preloadTorrent(any(), any()) } coAnswers {
            Result.Success(Unit)
        }
        every { torrserverManager.observeTorrserverStatus() } returns flowOf(TorrserverStatus.General.Started)
        val defaultMainComponent = getDefaultMainComponent()

        defaultMainComponent.uiState.test {
            assertFalse(awaitItem().isShowBufferization)
            defaultMainComponent.detailsComponent.showDetails("hash_for_test")
            defaultMainComponent.detailsComponent.contentFilesComponent.onClickItem(mockk(relaxed = true))
            assertTrue(awaitItem().isShowBufferization)
            assertFalse(awaitItem().isShowBufferization)
        }
    }

    @Test
    fun `Add torrent and show details then check isShowDetails is true`() = runTest {
        val torretnsResult: Result<List<Torrent>, TorrserverError> = Result.Success(emptyList())
        val torrserverStatus = MutableStateFlow<TorrserverStatus>(TorrserverStatus.General.Started)

        every { torrserverManager.observeTorrserverStatus() } returns torrserverStatus
        every { torrentApi.observeTorrents() } returns flowOf(torretnsResult)
        coEvery { torrentApi.getTorrents() } returns torretnsResult
        coEvery { addTorrentFileUseCase.invoke(any()) } coAnswers {
            Result.Success(mockk<Torrent>(relaxed = true))
        }
        coEvery { torrentApi.getTorrent(any()) } coAnswers { Result.Success(mockk(relaxed = true)) }

        val defaultMainComponent = getDefaultMainComponent()

        defaultMainComponent.uiState.test {
            assertTrue(awaitItem().serverStatus is ServerStatus.General.Started)
            defaultMainComponent.addTorrentAndShowDetails("path_to_torrent_file")
            assertTrue(awaitItem().isShowDetails)
        }
    }

    private fun getDefaultMainComponent() = DefaultMainComponent(
        context = DefaultComponentContext(lifecycle = lifecycle),
        torrentApi = torrentApi,
        dispatchers = dispatchers,
        torrserverManager = torrserverManager,
        searchingTmdb = searchingTmdb,
        addTorrentFileUseCase = addTorrentFileUseCase,
        addMagnetLinkUseCase = addMagnetLinkUseCase,
        tvEpisodesTmdb = tvEpisodesTmdb,
        findPosterUseCase = findPosterUseCase,
        appSettings = appSettings,
        tvSeasonTmdb = tvSeasonTmdb,
        openSettingsScreen = {},
        onClickPlayFile = {},
        navigateToDetails = navigateToDetails,
        localization = localization,
        fileUtils = fileUtils,
    )
}
