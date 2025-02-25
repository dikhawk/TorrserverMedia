package com.dik.torrentlist.main

import app.cash.turbine.test
import com.dik.common.AppDispatchers
import com.dik.common.Progress
import com.dik.common.ResultProgress
import com.dik.common.i18n.LocalizationResource
import com.dik.torrentlist.screens.main.torrserverbar.DefaultTorrserverBarComponent
import com.dik.torrentlist.screens.main.torrserverbar.TorrServerStarterPlatform
import com.dik.torrserverapi.TorrserverError
import com.dik.torrserverapi.model.TorrserverFile
import com.dik.torrserverapi.server.TorrserverCommands
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import torrservermedia.features.torrentlist.impl.generated.resources.Res
import torrservermedia.features.torrentlist.impl.generated.resources.main_error_msg_no_server_connection
import torrservermedia.features.torrentlist.impl.generated.resources.main_torrserver_bar_msg_installing_torrserver
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultTorrserverBarComponentTest {

    private val torrserverCommands: TorrserverCommands = mockk()
    private val unconfiedTestDispatcher = UnconfinedTestDispatcher()
    private val unconfiedDispatchers: AppDispatchers = object : AppDispatchers {
        override fun ioDispatcher() = unconfiedTestDispatcher
        override fun defaultDispatcher() = unconfiedTestDispatcher
        override fun mainDispatcher() = unconfiedTestDispatcher
    }
    private val unconfiedTestComponentScope = TestScope(unconfiedTestDispatcher)
    private val localization: LocalizationResource = mockk()
    private val torrServerStarter: TorrServerStarterPlatform = mockk()

    @Test
    fun `On click install server loading then check progress`() = runTest {
        val progressValue = 0.2
        val progressMessage = "progress server status"
        val resultFlow: ResultProgress<TorrserverFile, TorrserverError> =
            ResultProgress.Loading(Progress(progress = progressValue))
        val installServerStatus = MutableStateFlow(resultFlow)
        val component = defaultTorrserverBarComponent(unconfiedDispatchers, unconfiedTestComponentScope)

        coEvery { torrserverCommands.installServer() } returns installServerStatus
        coEvery {
            localization.getString(Res.string.main_torrserver_bar_msg_installing_torrserver)
        } returns progressMessage

        component.onClickInstallServer()

        component.uiState.test {
            val item = awaitItem()

            assertTrue(item.isShowProgress)
            assertEquals(progressValue.toFloat(), item.progressValue)
            assertEquals(progressMessage, item.serverStatusText)
        }
    }

    @Test
    fun `On click install server with error then check progress`() = runTest {
        val errorMessage = "error server status"
        val resultFlow: ResultProgress<TorrserverFile, TorrserverError> =
            ResultProgress.Error(TorrserverError.Server.NoServerConnection)
        val installServerStatus = MutableStateFlow(resultFlow)
        val component = defaultTorrserverBarComponent(unconfiedDispatchers, unconfiedTestComponentScope)

        coEvery { torrserverCommands.installServer() } returns installServerStatus
        coEvery {
            localization.getString(Res.string.main_error_msg_no_server_connection)
        } returns errorMessage

        component.onClickInstallServer()

        component.uiState.test {
            val item = awaitItem()

            assertFalse(item.isShowProgress)
            assertEquals(errorMessage, item.error)
        }
    }

    @Test
    fun `On click install server success then check progress`() = runTest {
        val resultFlow: ResultProgress<TorrserverFile, TorrserverError> =
            ResultProgress.Success(TorrserverFile(filePath = "path_to_file"))
        val installServerStatus = MutableStateFlow(resultFlow)
        val component = defaultTorrserverBarComponent(unconfiedDispatchers, unconfiedTestComponentScope)

        coEvery { torrserverCommands.installServer() } returns installServerStatus

        component.onClickInstallServer()

        coVerify { torrServerStarter.startTorrServer() }

        component.uiState.test {
            val item = awaitItem()

            assertFalse(item.isShowProgress)
            assertTrue(item.isServerInstalled)
        }
    }

    @Test
    fun `On click start torrserver`() = runTest {
        val component = defaultTorrserverBarComponent(unconfiedDispatchers, unconfiedTestComponentScope)

        component.onClickStartServer()

        coVerify { torrServerStarter.startTorrServer() }
    }

    private fun defaultTorrserverBarComponent(
        dispatchers: AppDispatchers, componentScope: CoroutineScope
    ) = DefaultTorrserverBarComponent(
        context = mockk(),
        torrserverCommands = torrserverCommands,
        dispatchers = dispatchers,
        componentScope = componentScope,
        localization = localization,
        torrServerStarter = torrServerStarter
    )
}