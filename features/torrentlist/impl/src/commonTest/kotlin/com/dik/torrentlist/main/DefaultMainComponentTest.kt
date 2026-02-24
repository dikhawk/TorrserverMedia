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
import com.dik.torrentlist.screens.main.DefaultMainComponent
import com.dik.torrentlist.screens.main.domain.AddMagnetLinkUseCase
import com.dik.torrentlist.screens.main.domain.AddTorrentFileUseCase
import com.dik.torrentlist.screens.main.domain.FindPosterUseCase
import com.dik.torrentlist.utils.FileUtils
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
import kotlinx.coroutines.flow.asSharedFlow
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
        coEvery { torrentApi.getTorrents() } returns Result.Success(listOf(mockk(relaxed = true)))
        coEvery { torrentApi.getTorrent(any()) } returns Result.Success(mockk(relaxed = true))

        val defaultMainComponent = geDefaultMainComponent()
        defaultMainComponent.torrentListComponent.onClickItem(mockk(relaxed = true))
        assertTrue(defaultMainComponent.uiState.value.isShowDetails)
    }

    @Test
    fun `On click item for COMPACT screen then check navigate to details screen`() = runTest {
        coEvery { torrentApi.getTorrents() } returns Result.Success(listOf(mockk(relaxed = true)))
        coEvery { torrentApi.getTorrent(any()) } returns Result.Success(mockk(relaxed = true))

        val defaultMainComponent = geDefaultMainComponent()
        defaultMainComponent.torrentListComponent.onNavigateToDetails(mockk(relaxed = true))

        verify(exactly = 1) { navigateToDetails(any(), any()) }
    }

    @Test
    fun `Change server status then check ui state serverStatus after change`() = runTest {
        coEvery { torrentApi.getTorrents() } returns Result.Success(emptyList())

        val defaultMainComponent = geDefaultMainComponent()

        defaultMainComponent.uiState.test {
            assertEquals(TorrserverStatus.Unknown("Init state"), awaitItem().serverStatus)
            torrServerStatusFlow.emit(TorrserverStatus.General.Running)
            assertEquals(TorrserverStatus.General.Running, awaitItem().serverStatus)
            lifecycle.destroy()
        }
    }

    @Test
    fun `On click play file then check bufferization is visible`() = runTest {
        coEvery { torrentApi.getTorrents() } returns Result.Success(emptyList())
        coEvery { torrentApi.getTorrent(any()) } returns Result.Success(mockk(relaxed = true))
        coEvery {
            searchingTmdb.multiSearching(query = any(), language = AppLanguage.RUSSIAN.iso)
        } returns Result.Success(listOf(mockk<Movie>(relaxed = true)))
        coEvery { torrentApi.preloadTorrent(any(), any()) } coAnswers {
            Result.Success(Unit)
        }
        val defaultMainComponent = geDefaultMainComponent()

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
        coEvery { torrentApi.getTorrents() } returns Result.Success(emptyList())
        coEvery { addTorrentFileUseCase.invoke(any()) } answers {
            Result.Success(mockk<Torrent>(relaxed = true))
        }
        coEvery { torrentApi.getTorrent(any()) } returns Result.Success(mockk(relaxed = true))

        val defaultMainComponent = geDefaultMainComponent()
        torrServerStatusFlow.emit(TorrserverStatus.General.Started)
        defaultMainComponent.addTorrentAndShowDetails("path_to_torrent_file")

        defaultMainComponent.uiState.test {
            assertTrue(awaitItem().isShowDetails)
        }
    }

    private fun geDefaultMainComponent() = DefaultMainComponent(
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
