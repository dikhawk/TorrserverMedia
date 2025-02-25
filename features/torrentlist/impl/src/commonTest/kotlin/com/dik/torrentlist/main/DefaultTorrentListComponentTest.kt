package com.dik.torrentlist.main

import com.arkivanov.decompose.ComponentContext
import com.dik.common.Result
import com.dik.torrentlist.screens.main.AddTorrentFile
import com.dik.torrentlist.screens.main.AddTorrentResult
import com.dik.torrentlist.screens.main.list.DefaultTorrentListComponent
import com.dik.torrentlist.utils.FileUtils
import com.dik.torrserverapi.TorrserverError
import com.dik.torrserverapi.server.TorrentApi
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DefaultTorrentListComponentTest {

    private val torrentApi: TorrentApi = mockk()
    private val addTorrentFile: AddTorrentFile = mockk()
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)
    private val fileUtils: FileUtils = mockk()
    private lateinit var defaultTorrentListComponent: DefaultTorrentListComponent

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        defaultTorrentListComponent = DefaultTorrentListComponent(
            context = mockk<ComponentContext>(),
            onTorrentClick = {},
            torrentApi = torrentApi,
            addTorrentFile = addTorrentFile,
            componentScope = testScope,
            fileUtils = fileUtils
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `should contain torrent after successful api call`() = testScope.runTest {
        coEvery { torrentApi.getTorrents() } returns Result.Success(listOf(mockk()))

        val uiState = defaultTorrentListComponent.uiState

        advanceTimeBy(3000)

        defaultTorrentListComponent.stopObservingTorrentList()

        assertTrue(uiState.value.torrents.size == 1)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `should contain error after unsuccessful api call`() = testScope.runTest {
        val error = TorrserverError.HttpError.ResponseReturnError("Response error")
        coEvery { torrentApi.getTorrents() } returns Result.Error(error)

        val uiState = defaultTorrentListComponent.uiState

        advanceTimeBy(3000)

        defaultTorrentListComponent.stopObservingTorrentList()

        assertEquals(uiState.value.error, error.toString())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `add torrent success and check show progress`() = testScope.runTest {
        val uriTorrent = kotlin.io.path.createTempFile("temp", "torrent").toUri()

        coEvery { addTorrentFile.invoke(any()) } returns AddTorrentResult(torrent = mockk())
        coEvery { fileUtils.uriToPath(uriTorrent.toString()) } returns uriTorrent.toString()
        defaultTorrentListComponent.stopObservingTorrentList()

        assertFalse(defaultTorrentListComponent.uiState.value.isShowProgress)

        defaultTorrentListComponent.addTorrents(listOf(uriTorrent.toString()))

        assertTrue(defaultTorrentListComponent.uiState.value.isShowProgress)

        advanceUntilIdle()

        assertFalse(defaultTorrentListComponent.uiState.value.isShowProgress)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `add torrent with error and check show progress`() = testScope.runTest {
        val uriTorrent = kotlin.io.path.createTempFile("temp", "torrent").toUri()

        coEvery { addTorrentFile.invoke(any()) } returns AddTorrentResult(error = "File not exist")
        coEvery { fileUtils.uriToPath(uriTorrent.toString()) } returns uriTorrent.toString()
        defaultTorrentListComponent.stopObservingTorrentList()

        assertFalse(defaultTorrentListComponent.uiState.value.isShowProgress)

        defaultTorrentListComponent.addTorrents(listOf(uriTorrent.toString()))

        assertTrue(defaultTorrentListComponent.uiState.value.isShowProgress)

        advanceUntilIdle()

        assertFalse(defaultTorrentListComponent.uiState.value.isShowProgress)
    }
}