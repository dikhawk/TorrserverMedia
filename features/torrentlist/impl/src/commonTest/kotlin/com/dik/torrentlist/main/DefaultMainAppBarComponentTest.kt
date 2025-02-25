package com.dik.torrentlist.main

import app.cash.turbine.test
import com.dik.common.AppDispatchers
import com.dik.common.i18n.LocalizationResource
import com.dik.torrentlist.screens.main.AddMagnetLink
import com.dik.torrentlist.screens.main.AddMagnetLinkResult
import com.dik.torrentlist.screens.main.AddTorrentFile
import com.dik.torrentlist.screens.main.appbar.DefaultMainAppBarComponent
import com.dik.torrentlist.screens.main.appbar.MainAppBarAction
import com.dik.torrentlist.utils.FileUtils
import com.dik.torrserverapi.model.TorrserverStatus
import com.dik.torrserverapi.server.TorrserverCommands
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import torrservermedia.features.torrentlist.impl.generated.resources.Res
import torrservermedia.features.torrentlist.impl.generated.resources.main_app_bar_error_server_not_started
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultMainAppBarComponentTest {

    private val unconfiedTestDispatcher = UnconfinedTestDispatcher()
    private val unconfiedTestComponentScope = TestScope(unconfiedTestDispatcher)

    private val standardTestDispatcher = UnconfinedTestDispatcher()
    private val standardDispatchers: AppDispatchers = object : AppDispatchers {
        override fun ioDispatcher() = standardTestDispatcher
        override fun defaultDispatcher() = standardTestDispatcher
        override fun mainDispatcher() = standardTestDispatcher
    }
    private val standardTestComponentScope = TestScope(standardTestDispatcher)

    private val addTorrentFile: AddTorrentFile = mockk()
    private val addMagnetLink: AddMagnetLink = mockk()
    private val torrserverCommands: TorrserverCommands = mockk()
    private val openSettingsScreen: () -> Unit = mockk(relaxed = true)
    private val localization: LocalizationResource = mockk()
    private val fileUtils: FileUtils = mockk()


    @Test
    fun `Observe status when server is started then isServerStarted == true`() = runTest {
        val statusFlow = MutableStateFlow(TorrserverStatus.UNKNOWN)
        coEvery { torrserverCommands.serverStatus() } returns statusFlow
        val component = defaultMainAppBarComponent(unconfiedTestComponentScope)

        component.uiState.test {
            assertFalse(awaitItem().isServerStarted)
            statusFlow.emit(TorrserverStatus.STARTED)
            assertTrue(awaitItem().isServerStarted)
        }
    }

    @Test
    fun `Open settings screen if server is started`() = runTest {
        val statusFlow = MutableStateFlow(TorrserverStatus.STARTED)
        coEvery { torrserverCommands.serverStatus() } returns statusFlow
        val component = defaultMainAppBarComponent(unconfiedTestComponentScope)

        component.openSettingsScreen()

        verify(exactly = 1) { openSettingsScreen() }
    }

    @Test
    fun `Open settings screen if server is not started`() = runTest {
        val errorName = "error_server_not_started"
        val statusFlow = MutableStateFlow(TorrserverStatus.STOPPED)
        coEvery { torrserverCommands.serverStatus() } returns statusFlow
        coEvery { localization.getString(Res.string.main_app_bar_error_server_not_started) } returns errorName
        val component = defaultMainAppBarComponent(unconfiedTestComponentScope)
        component.openSettingsScreen()

        component.uiState.test {
            assertEquals(errorName, awaitItem().error)
        }
        verify(exactly = 0) { openSettingsScreen() }
    }

    @Test
    fun `On link change then check ui state`() = runTest {
        val magnetLink = "magnet_link"
        val component = defaultMainAppBarComponent(unconfiedTestComponentScope)

        component.onLinkChanged(magnetLink)

        component.uiState.test {
            assertEquals(magnetLink, awaitItem().link)
        }
    }

    @Test
    fun `On link change after add link with error then check ui state`() = runTest {
        val magnetLink = "magnet_link"
        val error = "magnet_link_error"
        val statusFlow = MutableStateFlow(TorrserverStatus.STARTED)

        coEvery { torrserverCommands.serverStatus() } returns statusFlow
        coEvery { addMagnetLink.invoke(any()) } coAnswers { AddMagnetLinkResult(error = error) }

        val component = defaultMainAppBarComponent(standardTestComponentScope)

        component.onLinkChanged(magnetLink)

        component.uiState.test {
            component.addLink()
            assertEquals(magnetLink, awaitItem().link)
            assertEquals(error, awaitItem().errorLink)
        }
    }

    @Test
    fun `On link change after add link success then check dismiss dialog ui state`() = runTest {
        val magnetLink = "magnet_link"
        val statusFlow = MutableStateFlow(TorrserverStatus.STARTED)

        coEvery { torrserverCommands.serverStatus() } returns statusFlow
        coEvery { addMagnetLink.invoke(any()) } coAnswers
                { AddMagnetLinkResult(torrent = mockk(relaxed = true)) }

        val component = defaultMainAppBarComponent(standardTestComponentScope)

        component.onLinkChanged(magnetLink)

        component.uiState.test {
            assertEquals(magnetLink, awaitItem().link)

            component.addLink()
            assertEquals(MainAppBarAction.Undefined, awaitItem().action)
        }
    }

    @Test
    fun `Open add link dialog when server is not started then check ui state action`() = runTest {
        val statusFlow = MutableStateFlow(TorrserverStatus.STOPPED)

        coEvery { torrserverCommands.serverStatus() } returns statusFlow
        val component = defaultMainAppBarComponent(standardTestComponentScope)

        component.openAddLinkDialog()

        component.uiState.test {
            advanceUntilIdle()

            assertEquals(MainAppBarAction.Undefined, awaitItem().action)
        }
    }

    @Test
    fun `Open add link dialog when server is started then check ui state action after dismiss dialog`() = runTest {
        val statusFlow = MutableStateFlow(TorrserverStatus.STARTED)

        coEvery { torrserverCommands.serverStatus() } returns statusFlow
        val component = defaultMainAppBarComponent(standardTestComponentScope)

        component.openAddLinkDialog()

        component.uiState.test {
            assertEquals(MainAppBarAction.ShowAddLinkDialog, awaitItem().action)
            component.dismissDialog()
            assertEquals(MainAppBarAction.Undefined, awaitItem().action)
        }
    }

    @Test
    fun `Change server status from STOPPED to STARTED and check ui state isServerStarted`() = runTest {
        val statusFlow = MutableStateFlow(TorrserverStatus.STOPPED)

        coEvery { torrserverCommands.serverStatus() } returns statusFlow
        val component = defaultMainAppBarComponent(standardTestComponentScope)

        component.uiState.test {
            assertFalse(awaitItem().isServerStarted)
            statusFlow.emit(TorrserverStatus.STARTED)
            assertTrue(awaitItem().isServerStarted)
        }
    }

    @Test
    fun `Open file pick torrent when server is started then check ui state action`() = runTest {
        val statusFlow = MutableStateFlow(TorrserverStatus.STARTED)

        coEvery { torrserverCommands.serverStatus() } returns statusFlow
        val component = defaultMainAppBarComponent(standardTestComponentScope)

        component.openFilePickTorrent()

        component.uiState.test {
            val item = awaitItem()
            assertTrue(item.isServerStarted)
            assertEquals(MainAppBarAction.ShowFilePicker, item.action)
        }
    }

    @Test
    fun `Open file pick torrent when server is stopped then check ui state action`() = runTest {
        val statusFlow = MutableStateFlow(TorrserverStatus.STOPPED)
        val serverNotStartedError = "main_app_bar_error_server_not_started"

        coEvery { torrserverCommands.serverStatus() } returns statusFlow
        coEvery { localization.getString(Res.string.main_app_bar_error_server_not_started) } returns
                serverNotStartedError
        val component = defaultMainAppBarComponent(standardTestComponentScope)

        component.openFilePickTorrent()

        component.uiState.test {
            val item = awaitItem()
            assertFalse(item.isServerStarted)
            assertEquals(item.error, serverNotStartedError)
            assertEquals(MainAppBarAction.Undefined, item.action)
        }
    }

    @Test
    fun `Picked file then check action is undefined`() = runTest {
        val statusFlow = MutableStateFlow(TorrserverStatus.STARTED)
        val pathToTorrent = "path/to/file.torrent"

        coEvery { torrserverCommands.serverStatus() } returns statusFlow
        val component = defaultMainAppBarComponent(standardTestComponentScope)

        component.onFilePicked(pathToTorrent)

        coVerify { fileUtils.absolutPath(pathToTorrent) }

        component.uiState.test {
            val item = awaitItem()
            assertEquals(MainAppBarAction.Undefined, item.action)
        }
    }

    private fun defaultMainAppBarComponent(
        scope: CoroutineScope
    ) = DefaultMainAppBarComponent(
        context = mockk(),
        componentScope = scope,
        addTorrentFile = addTorrentFile,
        addMagnetLink = addMagnetLink,
        torrserverCommands = torrserverCommands,
        openSettingsScreen = openSettingsScreen,
        localization = localization,
        fileUtils = fileUtils,
    )
}