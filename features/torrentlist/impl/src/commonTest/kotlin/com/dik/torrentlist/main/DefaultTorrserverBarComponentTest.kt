package com.dik.torrentlist.main

import app.cash.turbine.test
import com.dik.common.AppDispatchers
import com.dik.common.i18n.LocalizationResource
import com.dik.torrentlist.screens.main.torrserverbar.DefaultTorrserverBarComponent
import com.dik.torrserverapi.TorrserverError
import com.dik.torrserverapi.server.TorrserverManager
import com.dik.torrserverapi.server.TorrserverStatus
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

    private val torrserverManager: TorrserverManager = mockk()
    private val unconfiedTestDispatcher = UnconfinedTestDispatcher()
    private val unconfiedDispatchers: AppDispatchers = object : AppDispatchers {
        override fun ioDispatcher() = unconfiedTestDispatcher
        override fun defaultDispatcher() = unconfiedTestDispatcher
        override fun mainDispatcher() = unconfiedTestDispatcher
    }
    private val unconfiedTestComponentScope = TestScope(unconfiedTestDispatcher)
    private val localization: LocalizationResource = mockk()

    @Test
    fun `On click install server loading then check progress`() = runTest {
        val progressValue = 0.2
        val progressMessage = "progress server status"
        val installServerStatus = MutableStateFlow(TorrserverStatus.Install.Progress(progressValue))
        val component =
            defaultTorrserverBarComponent(unconfiedDispatchers, unconfiedTestComponentScope)

        coEvery { torrserverManager.installOrUpdate() } returns installServerStatus
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
        val errorMessage = TorrserverError.Server.NoServerConnection.toString()
        val installServerStatus = MutableStateFlow(
            TorrserverStatus.Install.Error(TorrserverError.Server.NoServerConnection.toString())
        )
        val component =
            defaultTorrserverBarComponent(unconfiedDispatchers, unconfiedTestComponentScope)

        coEvery { torrserverManager.installOrUpdate() } returns installServerStatus
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
            val installServerStatus = MutableStateFlow(TorrserverStatus.Install.Installed)
            val component = defaultTorrserverBarComponent(unconfiedDispatchers, unconfiedTestComponentScope)

            coEvery { torrserverManager.installOrUpdate()} returns installServerStatus

            component.onClickInstallServer()

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

            coVerify { torrserverManager.start() }
        }

    private fun defaultTorrserverBarComponent(
        dispatchers: AppDispatchers, componentScope: CoroutineScope
    ) = DefaultTorrserverBarComponent(
        context = mockk(),
        torrserverManager = torrserverManager,
        dispatchers = dispatchers,
        componentScope = componentScope,
        localization = localization,
    )
}
