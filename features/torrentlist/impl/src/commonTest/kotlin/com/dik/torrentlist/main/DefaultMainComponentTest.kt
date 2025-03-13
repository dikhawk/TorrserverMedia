package com.dik.torrentlist.main

import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.window.core.layout.WindowSizeClass
import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.destroy
import com.dik.appsettings.api.model.AppSettings
import com.dik.common.AppDispatchers
import com.dik.common.Result
import com.dik.common.i18n.AppLanguage
import com.dik.common.i18n.LocalizationResource
import com.dik.common.platform.WindowAdaptiveClient
import com.dik.themoviedb.SearchTheMovieDbApi
import com.dik.themoviedb.TvEpisodesTheMovieDbApi
import com.dik.themoviedb.TvSeasonsTheMovieDbApi
import com.dik.themoviedb.model.Movie
import com.dik.torrentlist.screens.main.AddMagnetLink
import com.dik.torrentlist.screens.main.AddTorrentFile
import com.dik.torrentlist.screens.main.AddTorrentResult
import com.dik.torrentlist.screens.main.DefaultMainComponent
import com.dik.torrentlist.screens.main.torrserverbar.TorrServerStarterPlatform
import com.dik.torrentlist.utils.FileUtils
import com.dik.torrserverapi.model.TorrserverStatus
import com.dik.torrserverapi.server.TorrentApi
import com.dik.torrserverapi.server.TorrserverCommands
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
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
    private val torrserverCommands: TorrserverCommands = mockk {
        coEvery { serverStatus() } returns torrServerStatusFlow.asSharedFlow()
    }
    private val searchingTmdb: SearchTheMovieDbApi = mockk()
    private val addTorrentFile: AddTorrentFile = mockk()
    private val addMagnetLink: AddMagnetLink = mockk()
    private val tvEpisodesTmdb: TvEpisodesTheMovieDbApi = mockk()
    private val windowAdaptiveClient: WindowAdaptiveClient = mockk()
    private val appSettings: AppSettings = mockk(relaxed = true) {
        every { language } returns AppLanguage.RUSSIAN
    }
    private val tvSeasonTmdb: TvSeasonsTheMovieDbApi = mockk()
    private val navigateToDetails: (torrentHash: String, poster: String) -> Unit  = mockk(relaxed = true)
    private val localization: LocalizationResource = mockk()
    private val torrServerStarter: TorrServerStarterPlatform = mockk()
    private val fileUtils: FileUtils = mockk()


    @Test
    fun `On click item for not COMPACT screen then check isShowDetails is true`() = runTest {
        val middleSizeScreen = WindowSizeClass.compute(800.0f, 800.0f)
        coEvery { torrentApi.getTorrents() } returns Result.Success(listOf(mockk(relaxed = true)))
        coEvery { torrentApi.getTorrent(any()) } returns Result.Success(mockk(relaxed = true))
        every {
            windowAdaptiveClient.windowAdaptiveFlow()
        } returns MutableStateFlow(
            WindowAdaptiveInfo(middleSizeScreen, windowPosture = mockk())
        )

        val defaultMainComponent = geDefaultMainComponent()
        defaultMainComponent.torrentListComponent.onClickItem(mockk(relaxed = true))
        assertTrue(defaultMainComponent.uiState.value.isShowDetails)
    }

    @Test
    fun `On click item for COMPACT screen then check navigate to details screen`() = runTest {
        val compactSizeScreen = WindowSizeClass.compute(400.0f, 1000.0f)
        coEvery { torrentApi.getTorrents() } returns Result.Success(listOf(mockk(relaxed = true)))
        coEvery { torrentApi.getTorrent(any()) } returns Result.Success(mockk(relaxed = true))
        every {
            windowAdaptiveClient.windowAdaptiveFlow()
        } returns MutableStateFlow(
            WindowAdaptiveInfo(compactSizeScreen, windowPosture = mockk())
        )

        val defaultMainComponent = geDefaultMainComponent()
        defaultMainComponent.torrentListComponent.onClickItem(mockk(relaxed = true))

        verify(exactly = 1) { navigateToDetails(any(), any()) }
    }

    @Test
    fun `Change server status then check ui state serverStatus after change`() = runTest {
        coEvery { torrentApi.getTorrents() } returns Result.Success(emptyList())

        val defaultMainComponent = geDefaultMainComponent()

        defaultMainComponent.uiState.test {
            assertEquals(TorrserverStatus.UNKNOWN, awaitItem().serverStatus)
            torrServerStatusFlow.emit(TorrserverStatus.RUNNING)
            assertEquals(TorrserverStatus.RUNNING, awaitItem().serverStatus)
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
        val middleSizeScreen = WindowSizeClass.compute(800.0f, 800.0f)
        coEvery { torrentApi.getTorrents() } returns Result.Success(emptyList())
        coEvery { addTorrentFile.invoke(any()) } answers { AddTorrentResult(torrent = mockk(relaxed = true)) }
        coEvery { torrentApi.getTorrent(any()) } returns Result.Success(mockk(relaxed = true))
        every {
            windowAdaptiveClient.windowAdaptiveFlow()
        } returns MutableStateFlow(
            WindowAdaptiveInfo(middleSizeScreen, windowPosture = mockk())
        )

        val defaultMainComponent = geDefaultMainComponent()
        torrServerStatusFlow.emit(TorrserverStatus.STARTED)
        defaultMainComponent.addTorrentAndShowDetails("path_to_torrent_file")

        defaultMainComponent.uiState.test {
            assertTrue(awaitItem().isShowDetails)
        }
    }

    private fun geDefaultMainComponent() = DefaultMainComponent(
        context = DefaultComponentContext(lifecycle = lifecycle),
        torrentApi = torrentApi,
        dispatchers = dispatchers,
        torrserverCommands = torrserverCommands,
        searchingTmdb = searchingTmdb,
        addTorrentFile = addTorrentFile,
        addMagnetLink = addMagnetLink,
        tvEpisodesTmdb = tvEpisodesTmdb,
        windowAdaptiveClient = windowAdaptiveClient,
        appSettings = appSettings,
        tvSeasonTmdb = tvSeasonTmdb,
        openSettingsScreen = {},
        onClickPlayFile = {},
        navigateToDetails = navigateToDetails,
        localization = localization,
        torrServerStarter = torrServerStarter,
        fileUtils = fileUtils,
    )
}