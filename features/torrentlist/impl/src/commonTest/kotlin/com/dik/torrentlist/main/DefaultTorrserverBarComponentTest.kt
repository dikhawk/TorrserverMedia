package com.dik.torrentlist.main

import app.cash.turbine.test
import com.dik.common.AppDispatchers
import com.dik.common.converter.toReadableSize
import com.dik.common.i18n.LocalizationResource
import com.dik.torrentlist.screens.main.torrserverbar.DefaultTorrserverBarComponent
import com.dik.torrentlist.screens.main.torrserverbar.InstallingState
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
import kotlin.test.Test
import kotlin.test.assertEquals
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
        val progressValue = 2.0
        val currentBytesValue = 300L
        val totalBytesValue = 600L

        val installServerStatus = MutableStateFlow(
            TorrserverStatus.Install.Progress(
                progressValue,
                currentBytesValue,
                totalBytesValue
            )
        )
        val component =
            defaultTorrserverBarComponent(unconfiedDispatchers, unconfiedTestComponentScope)

        coEvery { torrserverManager.installOrUpdate() } returns installServerStatus

        component.uiState.test {
            skipItems(1)
            component.onClickInstallServer()
            skipItems(1)
            val item = awaitItem().installingState as InstallingState.Installing

            assertEquals(progressValue.toFloat() / 100f, item.progress)
            assertEquals(currentBytesValue.toReadableSize(), item.currentBytes)
            assertEquals(totalBytesValue.toReadableSize(), item.totalBytes)
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

        component.uiState.test {
            skipItems(1)
            component.onClickInstallServer()
            skipItems(1)
            val item = awaitItem().installingState as InstallingState.Error

            assertEquals(errorMessage, item.msg)
        }
    }

    @Test
    fun `On click install server success then check progress`() = runTest {
        val installServerStatus = MutableStateFlow(TorrserverStatus.Install.Installed)
        val component =
            defaultTorrserverBarComponent(unconfiedDispatchers, unconfiedTestComponentScope)

        coEvery { torrserverManager.installOrUpdate() } returns installServerStatus

        component.uiState.test {
            skipItems(1)
            component.onClickInstallServer()
            skipItems(1)

            assertTrue(awaitItem().installingState is InstallingState.Installed)
        }
    }

    @Test
    fun `On click start torrserver`() = runTest {
        val component =
            defaultTorrserverBarComponent(unconfiedDispatchers, unconfiedTestComponentScope)

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
