package com.dik.torrentlist.main

import app.cash.turbine.test
import com.dik.common.Result
import com.dik.common.i18n.LocalizationResource
import com.dik.torrentlist.screens.main.appbar.DefaultMainAppBarComponent
import com.dik.torrentlist.screens.main.appbar.MainAppBarAction
import com.dik.torrentlist.screens.main.domain.AddMagnetLinkErrors
import com.dik.torrentlist.screens.main.domain.AddMagnetLinkUseCase
import com.dik.torrentlist.screens.main.domain.AddTorrentFileUseCase
import com.dik.torrentlist.utils.FileUtils
import com.dik.torrserverapi.model.Torrent
import com.dik.torrserverapi.server.TorrserverManager
import com.dik.torrserverapi.server.TorrserverStatus
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
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

    private val standardTestDispatcher = StandardTestDispatcher()
    private val standardTestComponentScope = TestScope(standardTestDispatcher)

    private val addTorrentFileUseCase: AddTorrentFileUseCase = mockk()
    private val addMagnetLinkUseCase: AddMagnetLinkUseCase = mockk()
    private val torrserverManager: TorrserverManager = mockk(relaxed = true)
    private val openSettingsScreen: () -> Unit = mockk()
    private val localization: LocalizationResource = mockk()
    private val fileUtils: FileUtils = mockk()


    @Test
    fun `Observe status when server is started then isServerStarted == true`() = runTest {
        val statusFlow: MutableStateFlow<TorrserverStatus> = MutableStateFlow(TorrserverStatus.General.Stopped)
        every { torrserverManager.observeTorrserverStatus() } returns statusFlow
        val component = defaultMainAppBarComponent(unconfiedTestComponentScope)

        component.uiState.test {
            assertFalse(awaitItem().isServerStarted)
            statusFlow.emit(TorrserverStatus.General.Started)
            assertTrue(awaitItem().isServerStarted)
        }
    }

    @Test
    fun `Open settings screen if server is started`() = runTest {
        val statusFlow = MutableStateFlow<TorrserverStatus>(TorrserverStatus.General.Started)

        every { torrserverManager.observeTorrserverStatus() } returns statusFlow
        every { openSettingsScreen.invoke() } returns Unit
        val component = defaultMainAppBarComponent(unconfiedTestComponentScope)

        component.uiState.test {
            assertTrue(awaitItem().isServerStarted)
            component.openSettingsScreen()
        }

        verify(exactly = 1) { openSettingsScreen.invoke() }
    }

    @Test
    fun `Open settings screen if server is not started`() = runTest {
        val errorName = "error_server_not_started"
        val statusFlow = MutableStateFlow(TorrserverStatus.General.Stopped)
        every { torrserverManager.observeTorrserverStatus() } returns statusFlow
        coEvery { localization.getString(Res.string.main_app_bar_error_server_not_started) } returns errorName
        every { openSettingsScreen.invoke() } returns Unit
        val component = defaultMainAppBarComponent(unconfiedTestComponentScope)

        component.uiState.test {
            assertFalse(awaitItem().isServerStarted)
            component.openSettingsScreen()
            assertEquals(errorName, awaitItem().error)
        }
        verify(exactly = 0) { openSettingsScreen.invoke() }
    }

    @Test
    fun `On link change then check ui state`() = runTest {
        val magnetLink = "magnet_link"
        val statusFlow: MutableStateFlow<TorrserverStatus> = MutableStateFlow(TorrserverStatus.General.Started)
        every { torrserverManager.observeTorrserverStatus() } returns statusFlow
        val component = defaultMainAppBarComponent(unconfiedTestComponentScope)

        component.uiState.test {
            skipItems(1)
            component.onLinkChanged(magnetLink)
            assertEquals(magnetLink, awaitItem().link)
        }
    }

    @Test
    fun `On link change after add link with error then check ui state`() = runTest {
        val magnetLink = "magnet_link"
        val error = AddMagnetLinkErrors.InvalidMagnet
        val statusFlow = MutableStateFlow(TorrserverStatus.General.Started)

        every { torrserverManager.observeTorrserverStatus() } returns statusFlow
        coEvery { addMagnetLinkUseCase.invoke(any()) } coAnswers { Result.Error(error) }

        val component = defaultMainAppBarComponent(unconfiedTestComponentScope)

        component.uiState.test {
            skipItems(1)
            component.onLinkChanged(magnetLink)
            component.addLink()
            assertEquals(magnetLink, awaitItem().link)
            val item = awaitItem()
            assertEquals(error.toString(), item.errorLink)
        }
    }

    @Test
    fun `On link change after add link success then check dismiss dialog ui state`() = runTest {
        val magnetLink = "magnet_link"
        val statusFlow = MutableStateFlow(TorrserverStatus.General.Started)

        every { torrserverManager.observeTorrserverStatus() } returns statusFlow
        coEvery { addMagnetLinkUseCase.invoke(any()) } coAnswers
                { Result.Success(mockk<Torrent>(relaxed = true)) }

        val component = defaultMainAppBarComponent(unconfiedTestComponentScope)

        component.uiState.test {
            skipItems(1)
            component.onLinkChanged(magnetLink)
            assertEquals(magnetLink, awaitItem().link)

            component.addLink()
            assertEquals(MainAppBarAction.Undefined, awaitItem().action)
        }
    }

    @Test
    fun `Open add link dialog when server is not started then check ui state action`() = runTest {
        val msgError = "Server not started"
        val statusFlow = MutableStateFlow(TorrserverStatus.General.Stopped)

        every { torrserverManager.observeTorrserverStatus() } returns statusFlow
        coEvery { localization.getString(Res.string.main_app_bar_error_server_not_started) } coAnswers { msgError }
        val component = defaultMainAppBarComponent(unconfiedTestComponentScope)

        component.uiState.test {
            skipItems(1)
            component.openAddLinkDialog()

            val item = awaitItem()
            assertEquals(msgError, item.error)
            assertEquals(MainAppBarAction.Undefined, item.action)
        }
    }

    @Test
    fun `Open add link dialog when server is started then check ui state action after dismiss dialog`() = runTest {
        val statusFlow = MutableStateFlow<TorrserverStatus>(TorrserverStatus.General.Started)

        every { torrserverManager.observeTorrserverStatus() } returns statusFlow
        val component = defaultMainAppBarComponent(unconfiedTestComponentScope)

        component.uiState.test {
            assertEquals(MainAppBarAction.Undefined, awaitItem().action)
            statusFlow.value = TorrserverStatus.General.Started
            component.openAddLinkDialog()
            assertEquals(MainAppBarAction.ShowAddLinkDialog, awaitItem().action)
            component.dismissDialog()
            assertEquals(MainAppBarAction.Undefined, awaitItem().action)
        }
    }

    @Test
    fun `Change server status from STOPPED to STARTED and check ui state isServerStarted`() = runTest {
        val statusFlow: MutableStateFlow<TorrserverStatus> =
            MutableStateFlow(TorrserverStatus.General.Stopped)

        every { torrserverManager.observeTorrserverStatus() } returns statusFlow
        val component = defaultMainAppBarComponent(unconfiedTestComponentScope)

        component.uiState.test {
            assertFalse(awaitItem().isServerStarted)
            statusFlow.emit(TorrserverStatus.General.Started)
            assertTrue(awaitItem().isServerStarted)
        }
    }

    @Test
    fun `Open file pick torrent when server is started then check ui state action`() = runTest {
        val statusFlow: MutableStateFlow<TorrserverStatus> =
            MutableStateFlow(TorrserverStatus.General.Started)

        every { torrserverManager.observeTorrserverStatus() } returns statusFlow
        val component = defaultMainAppBarComponent(unconfiedTestComponentScope)


        component.uiState.test {
            assertEquals(MainAppBarAction.Undefined, awaitItem().action)
            component.openFilePickTorrent()
            val item = awaitItem()
            assertTrue(item.isServerStarted)
            assertEquals(MainAppBarAction.ShowFilePicker, item.action)
        }
    }

    @Test
    fun `Open file pick torrent when server is stopped then check ui state action`() = runTest {
        val statusFlow = MutableStateFlow(TorrserverStatus.General.Stopped)
        val serverNotStartedError = "main_app_bar_error_server_not_started"

        every { torrserverManager.observeTorrserverStatus() } returns statusFlow
        coEvery { localization.getString(Res.string.main_app_bar_error_server_not_started) } returns
                serverNotStartedError
        val component = defaultMainAppBarComponent(unconfiedTestComponentScope)

        component.uiState.test {
            skipItems(1)
            component.openFilePickTorrent()
            val item = awaitItem()
            assertFalse(item.isServerStarted)
            assertEquals(item.error, serverNotStartedError)
            assertEquals(MainAppBarAction.Undefined, item.action)
        }
    }

    @Test
    fun `Picked file then check action is undefined`() = runTest {
        val statusFlow = MutableStateFlow(TorrserverStatus.General.Started)
        val pathToTorrent = "path/to/file.torrent"

        every { torrserverManager.observeTorrserverStatus() } returns statusFlow
        val component = defaultMainAppBarComponent(unconfiedTestComponentScope)

        component.uiState.test {
            component.onFilePicked(pathToTorrent)

            coVerify { fileUtils.absolutPath(pathToTorrent) }
            val item = awaitItem()
            assertEquals(MainAppBarAction.Undefined, item.action)
        }
    }

    private fun defaultMainAppBarComponent(
        scope: CoroutineScope
    ) = DefaultMainAppBarComponent(
        context = mockk(),
        componentScope = scope,
        addTorrentFileUseCase = addTorrentFileUseCase,
        addMagnetLinkUseCase = addMagnetLinkUseCase,
        torrserverManager = torrserverManager,
        openSettingsScreen = openSettingsScreen,
        localization = localization,
        fileUtils = fileUtils,
    )
}