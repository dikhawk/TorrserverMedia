package com.dik.torrentlist.main

import app.cash.turbine.test
import com.arkivanov.decompose.ComponentContext
import com.dik.common.Result
import com.dik.torrentlist.screens.main.domain.AddMagnetLinkUseCase
import com.dik.torrentlist.screens.main.domain.AddTorrentFileErrors
import com.dik.torrentlist.screens.main.domain.AddTorrentFileUseCase
import com.dik.torrentlist.screens.main.list.DefaultTorrentListComponent
import com.dik.torrentlist.utils.FileUtils
import com.dik.torrserverapi.TorrserverError
import com.dik.torrserverapi.model.Torrent
import com.dik.torrserverapi.server.api.TorrentApi
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DefaultTorrentListComponentTest {

    private val torrentApi: TorrentApi = mockk()
    private val addTorrentFileUseCase: AddTorrentFileUseCase = mockk()
    private val addMagnetLinkUseCase: AddMagnetLinkUseCase = mockk()

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private val unconfiedTestDispatcher = UnconfinedTestDispatcher()
    private val unconfiedTestComponentScope = TestScope(unconfiedTestDispatcher)

    private val fileUtils: FileUtils = mockk()


    @Test
    fun `should contain torrent after successful api call`() = runTest {
        val torrentsFlow: MutableStateFlow<Result<List<Torrent>, TorrserverError>> =
            MutableStateFlow(Result.Success(emptyList()))
        coEvery { torrentApi.observeTorrents(any()) } returns torrentsFlow

        val component = defaultTorrentListComponent(unconfiedTestComponentScope)

        component.uiState.test {
            assertTrue(awaitItem().torrents.isEmpty())

            torrentsFlow.update { Result.Success(listOf(mockk<Torrent>(relaxed = true))) }

            assertFalse(awaitItem().torrents.isEmpty())
        }
    }

    @Test
    fun `should contain error after unsuccessful api call`() = runTest {
        val error = TorrserverError.HttpError.ResponseReturnError("Response error")
        val torrentsFlow: MutableStateFlow<Result<List<Torrent>, TorrserverError>> =
            MutableStateFlow(Result.Success(emptyList()))
        coEvery { torrentApi.observeTorrents() } returns torrentsFlow

        val component = defaultTorrentListComponent(unconfiedTestComponentScope)
        val uiState = component.uiState

        uiState.test {
            assertEquals(awaitItem().error, null)
            torrentsFlow.update { Result.Error(error) }
            assertEquals(awaitItem().error, error.toString())
        }
    }

    @Test
    fun `add torrent success and check show progress`() = runTest {
        val uriTorrent = "file:/home/user/test.torrent"
        val torrentsFlow: MutableStateFlow<Result<List<Torrent>, TorrserverError>> =
            MutableStateFlow(Result.Success(emptyList()))

        coEvery { addTorrentFileUseCase.invoke(any()) } coAnswers {
            delay(100)
            Result.Success(mockk<Torrent>(relaxed = true))
        }

        coEvery { fileUtils.uriToPath(uriTorrent) } returns uriTorrent
        every { torrentApi.observeTorrents(any()) } returns torrentsFlow

        val component = defaultTorrentListComponent(unconfiedTestComponentScope)

        component.uiState.test {
            assertTrue(awaitItem().isShowProgress)
            torrentsFlow.value = Result.Success(listOf(mockk<Torrent>(relaxed = true)))
            component.addTorrents(listOf(uriTorrent))
            assertFalse(awaitItem().isShowProgress)
        }
    }

    @Test
    fun `add torrent with error and check show progress`() = runTest {
        val uriTorrent = "file:/home/user/test.torrent"
        val torrentsFlow: MutableStateFlow<Result<List<Torrent>, TorrserverError>> =
            MutableStateFlow(Result.Success(emptyList()))

        coEvery { addTorrentFileUseCase.invoke(any()) } coAnswers {
            Result.Error(AddTorrentFileErrors.TorrentNotExist)
        }
        coEvery { fileUtils.uriToPath(uriTorrent) } returns uriTorrent
        every { torrentApi.observeTorrents(any()) } returns torrentsFlow

        val component = defaultTorrentListComponent(unconfiedTestComponentScope)

        component.uiState.test {
            assertTrue(awaitItem().isShowProgress)
            torrentsFlow.value = Result.Success(listOf(mockk<Torrent>(relaxed = true)))
            component.addTorrents(listOf(uriTorrent))
            assertFalse(expectMostRecentItem().isShowProgress)
        }
    }

    private fun defaultTorrentListComponent(
        scope: CoroutineScope
    ) = DefaultTorrentListComponent(
        context = mockk<ComponentContext>(relaxed = true),
        onTorrentClick = {},
        onNavigateToDetails = {},
        onTorrentsIsEmpty = {},
        torrentApi = torrentApi,
        addTorrentFileUseCase = addTorrentFileUseCase,
        addMagnetLinkUseCase = addMagnetLinkUseCase,
        componentScope = scope,
        fileUtils = fileUtils
    )
}
