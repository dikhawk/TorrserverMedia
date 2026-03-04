package com.dik.settings.main

import app.cash.turbine.test
import com.dik.appsettings.api.model.AppSettings
import com.dik.common.AppDispatchers
import com.dik.common.Result
import com.dik.common.i18n.AppLanguage
import com.dik.common.i18n.LocalizationResource
import com.dik.common.player.Player
import com.dik.common.utils.cpuArch
import com.dik.common.utils.platformName
import com.dik.settings.utils.mbToBytes
import com.dik.settings.utils.toIntOrZero
import com.dik.settings.utils.toLongOrZero
import com.dik.torrserverapi.TorrserverError
import com.dik.torrserverapi.model.ServerSettings
import com.dik.torrserverapi.server.TorrserverManager
import com.dik.torrserverapi.server.TorrserverStatus
import com.dik.torrserverapi.server.api.ServerSettingsApi
import com.dik.torrserverapi.server.api.TorrserverApiClient
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import torrservermedia.features.settings.impl.generated.resources.Res
import torrservermedia.features.settings.impl.generated.resources.main_settings_available_new_version
import torrservermedia.features.settings.impl.generated.resources.main_settings_available_new_version_update_error
import torrservermedia.features.settings.impl.generated.resources.main_settings_available_new_version_update_loading
import torrservermedia.features.settings.impl.generated.resources.main_settings_available_new_version_update_success
import torrservermedia.features.settings.impl.generated.resources.main_settings_snackbar_default_settings
import torrservermedia.features.settings.impl.generated.resources.main_settings_snackbar_not_available_now
import torrservermedia.features.settings.impl.generated.resources.main_settings_snackbar_save
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MainComponentTest {

    private val serverSettingsApi: ServerSettingsApi = mockk()
    private val standardTestDispatcher = StandardTestDispatcher()
    private val standartdDispathcers = object : AppDispatchers {
        override fun ioDispatcher() = standardTestDispatcher
        override fun defaultDispatcher() = standardTestDispatcher
        override fun mainDispatcher() = standardTestDispatcher
    }

    private val appSettings: AppSettings = mockk(relaxed = true) {
        every { defaultPlayer } returns Player.VLC
        every { language } returns AppLanguage.ENGLISH
    }
    private val torrserverApiClient: TorrserverApiClient = mockk()
    private val torrserverManager: TorrserverManager = mockk()
    private val localization: LocalizationResource = mockk()
    private val onFinish: () -> Unit = mockk(relaxed = true)


    @Test
    fun `Load Settings success and check ui state`() = runTest(standardTestDispatcher) {
        val component = defaultMainComponent(dispatchers = standartdDispathcers)
        val serverSettings: ServerSettings = mockk(relaxed = true) {
            every { cacheSize } returns 1024000L
            every { readerReadAHead } returns 12
            every { preloadCache } returns 2048
            every { ipv6 } returns true
            every { tcp } returns true
            every { mtp } returns true
            every { pex } returns true
            every { encryptionHeader } returns true
            every { timeoutConnection } returns 10000
            every { torrentConnections } returns 30
            every { dht } returns true
            every { upnp } returns true
            every { limitSpeedDownload } returns 4096
            every { incomingConnection } returns 2
            every { distribution } returns true
            every { limitSpeedDistribution } returns 12496
            every { dlna } returns true
            every { dlnaName } returns "My Perfect Dlna"
        }
        coEvery { serverSettingsApi.getSettings() } coAnswers {
            Result.Success(serverSettings)
        }
        coEvery { torrserverApiClient.echo() } coAnswers {
            Result.Success("Torrserver")
        }
        every { torrserverManager.checkNewVersion() } returns flow {
            emit(TorrserverStatus.CheckLatestVersion.VersionIsActual)
        }

        component.uiState.test {
            val itemBefore = awaitItem()

            assertFalse(itemBefore.isShowProgressBar)

            component.loadSettings()
            skipItems(1)
            val item = awaitItem()

            assertTrue(item.isShowProgressBar)
            assertEquals("${platformName().osname} ${cpuArch()}", item.operationSystem)
            assertEquals(appSettings.defaultPlayer, item.player)
            assertEquals(appSettings.language, item.language)
            assertTrue(item.playersList.isNotEmpty())
            assertEquals((serverSettings.cacheSize / 1024L / 1024L).toString(), item.cacheSize)
            assertEquals(serverSettings.readerReadAHead.toString(), item.readerReadAHead)
            assertEquals(serverSettings.preloadCache.toString(), item.preloadCache)
            assertEquals(serverSettings.ipv6, item.ipv6)
            assertEquals(serverSettings.tcp, item.tcp)
            assertEquals(serverSettings.mtp, item.mtp)
            assertEquals(serverSettings.pex, item.pex)
            assertEquals(serverSettings.encryptionHeader, item.encryptionHeader)
            assertEquals(serverSettings.timeoutConnection.toString(), item.timeoutConnection)
            assertEquals(serverSettings.torrentConnections.toString(), item.torrentConnections)
            assertEquals(serverSettings.dht, item.dht)
            assertEquals(serverSettings.upnp, item.upnp)
            assertEquals(serverSettings.limitSpeedDownload.toString(), item.limitSpeedDownload)
            assertEquals(serverSettings.incomingConnection.toString(), item.incomingConnection)
            assertEquals(serverSettings.distribution, item.distribution)
            assertEquals(
                serverSettings.limitSpeedDistribution.toString(),
                item.limitSpeedDistribution
            )
            assertEquals(serverSettings.dlna, item.dlna)
            assertEquals(serverSettings.dlnaName, item.dlnaName)

            val itemAfter = awaitItem()
            assertFalse(itemAfter.isShowProgressBar)
        }
    }

    @Test
    fun `Load Settings failure and check ui state`() = runTest(standardTestDispatcher) {
        val component = defaultMainComponent(dispatchers = standartdDispathcers)

        coEvery { serverSettingsApi.getSettings() } coAnswers {
            Result.Error(TorrserverError.HttpError.ResponseReturnNull)
        }
        coEvery { torrserverApiClient.echo() } coAnswers {
            Result.Success("Torrserver")
        }
        every { torrserverManager.checkNewVersion() } returns flow {
            emit(TorrserverStatus.CheckLatestVersion.VersionIsActual)
        }

        component.uiState.test {
            assertFalse(awaitItem().isShowProgressBar)
            component.loadSettings()
            skipItems(1)

            val item = awaitItem()

            assertTrue(item.isShowProgressBar)
            assertEquals(TorrserverError.HttpError.ResponseReturnNull.toString(), item.snackbar)
            assertFalse(awaitItem().isShowProgressBar)
        }
    }

    @Test
    fun `Load settings and check updates when available new version`() =
        runTest(standardTestDispatcher) {
            val component = defaultMainComponent(dispatchers = standartdDispathcers)

            val isAvailableNewVersion = "Is available new version"
            coEvery { serverSettingsApi.getSettings() } coAnswers {
                Result.Success(mockk(relaxed = true))
            }
            coEvery { torrserverApiClient.echo() } coAnswers {
                Result.Success("Torrserver")
            }
            coEvery { torrserverManager.checkNewVersion() } returns flow {
                emit(TorrserverStatus.CheckLatestVersion.AvailableNewVersion("new_version"))
            }
            coEvery {
                localization.getString(Res.string.main_settings_available_new_version)
            } coAnswers {
                isAvailableNewVersion
            }

            component.uiState.test {
                component.loadSettings()
                skipItems(3)
                val item = awaitItem()

                assertEquals(isAvailableNewVersion, item.availableNewVersionText)
                assertTrue(item.isAvailableNewVersion)
                assertFalse(awaitItem().isShowProgressBar)
            }
        }

    @Test
    fun `On click back button and check onFinsh`() = runTest {
        val component = defaultMainComponent()

        component.onClickBack()

        verify { onFinish.invoke() }
    }

    @Test
    fun `On click update torrserver loading`() = runTest(standardTestDispatcher) {
        val installServerState = MutableStateFlow(
            TorrserverStatus.Install.Progress(progress = 0.35, totalBytes = 1024, currentBytes = 64)
        )
        val updateProgressMsg = "Update torrserver progress %s %%"
        val component = defaultMainComponent(dispatchers = standartdDispathcers)

        coEvery { torrserverApiClient.echo() } coAnswers { Result.Success("Torrserver") }
        coEvery { torrserverManager.installOrUpdate() } returns installServerState
        coEvery {
            localization.getString(Res.string.main_settings_available_new_version_update_loading)
        } coAnswers {
            updateProgressMsg
        }
        coEvery { serverSettingsApi.getSettings() } coAnswers { Result.Success(mockk(relaxed = true)) }
        coEvery { torrserverManager.checkNewVersion() } returns flow {
            emit(TorrserverStatus.CheckLatestVersion.AvailableNewVersion("new_version"))
        }

        component.uiState.test {
            val beforeStartLoading = awaitItem()

            assertFalse(beforeStartLoading.isShowAvailableNewVersionProgress)

            component.onClickUpdateTorrserver()

            skipItems(1)
            val afterStartLoading = awaitItem()

            assertEquals(
                updateProgressMsg.format(0.35.toString()),
                afterStartLoading.availableNewVersionText
            )
        }
    }

    @Test
    fun `On click update torrserver error`() = runTest(standardTestDispatcher) {
        val installServerState = MutableStateFlow(
            TorrserverStatus.Install.Error(TorrserverError.HttpError.ResponseReturnNull.toString())
        )
        val updateErrorMsg = "Update error"
        val component = defaultMainComponent(dispatchers = standartdDispathcers)

        coEvery { torrserverApiClient.echo() } coAnswers { Result.Success("Torrserver") }
        coEvery { torrserverManager.installOrUpdate() } returns installServerState
        coEvery {
            localization.getString(Res.string.main_settings_available_new_version_update_error)
        } coAnswers {
            updateErrorMsg
        }
        coEvery { serverSettingsApi.getSettings() } coAnswers { Result.Success(mockk(relaxed = true)) }
        coEvery { torrserverManager.checkNewVersion() } returns
                flowOf(TorrserverStatus.CheckLatestVersion.VersionIsActual)

        component.uiState.test {
            assertFalse(awaitItem().isShowAvailableNewVersionProgress)

            component.onClickUpdateTorrserver()

            skipItems(1)

            val item = awaitItem()
            assertEquals(updateErrorMsg, item.availableNewVersionText)
            assertFalse(item.isShowAvailableNewVersionProgress)
        }
    }

    @Test
    fun `On click update torrserver success`() = runTest(standardTestDispatcher) {
        val installServerState = MutableStateFlow(TorrserverStatus.Install.Installed)
        val updateSuccess = "Update is success"
        val component = defaultMainComponent(dispatchers = standartdDispathcers)

        coEvery { torrserverApiClient.echo() } coAnswers { Result.Success("Torrserver") }
        coEvery { torrserverManager.installOrUpdate() } returns installServerState
        coEvery {
            localization.getString(Res.string.main_settings_available_new_version_update_success)
        } coAnswers {
            updateSuccess
        }
        coEvery { serverSettingsApi.getSettings() } coAnswers { Result.Success(mockk(relaxed = true)) }
        coEvery { torrserverManager.checkNewVersion() } returns
                flowOf(TorrserverStatus.CheckLatestVersion.AvailableNewVersion("new_version"))
        coEvery { torrserverManager.restart() } returns flow {
            emit(TorrserverStatus.General.Started)
        }

        component.uiState.test {
            assertFalse(awaitItem().isShowAvailableNewVersionProgress)

            component.onClickUpdateTorrserver()

            skipItems(1)
            val item = awaitItem()
            assertEquals(updateSuccess, item.availableNewVersionText)
            assertFalse(item.isAvailableNewVersion)
            assertFalse(item.isShowAvailableNewVersionProgress)
        }

        coVerify { torrserverManager.restart() }
    }

    @Test
    fun `On change default player`() = runTest(standardTestDispatcher) {
        val component = defaultMainComponent(dispatchers = standartdDispathcers)
        val players = Player.entries
        val randomPlayer = players[Random.nextInt(0, players.size - 1)]

        component.uiState.test {
            assertEquals(Player.DEFAULT_PLAYER, awaitItem().player)

            component.onChangeDefaultPlayer(randomPlayer)

            assertEquals(randomPlayer, awaitItem().player)
        }
    }

    @Test
    fun `On change cache size when is only digit`() = runTest(standardTestDispatcher) {
        val component = defaultMainComponent(dispatchers = standartdDispathcers)
        val value = "1234"

        component.uiState.test {
            assertEquals("0", awaitItem().cacheSize)
            component.onChangeCacheSize(value)
            assertEquals(value, awaitItem().cacheSize)
        }
    }

    @Test
    fun `On change cache size when is digit and letters`() = runTest(standardTestDispatcher) {
        val component = defaultMainComponent(dispatchers = standartdDispathcers)
        val value = "1234fiew"

        component.uiState.test {
            assertEquals("0", awaitItem().cacheSize)
            component.onChangeCacheSize(value)
            expectNoEvents()
        }
    }

    @Test
    fun `On change reader read a head when is only digit`() = runTest(standardTestDispatcher) {
        val component = defaultMainComponent(dispatchers = standartdDispathcers)
        val value = "677"

        component.uiState.test {
            assertEquals("0", awaitItem().readerReadAHead)
            component.onChangeReaderReadAHead(value)
            assertEquals(value, awaitItem().readerReadAHead)
        }
    }

    @Test
    fun `On change reader read a head when is digit and letters`() =
        runTest(standardTestDispatcher) {
            val component = defaultMainComponent(dispatchers = standartdDispathcers)
            val value = "1234fiew"

            component.uiState.test {
                assertEquals("0", awaitItem().readerReadAHead)
                component.onChangeReaderReadAHead(value)
                expectNoEvents()
            }
        }

    @Test
    fun `On change preload cache when is only digit`() = runTest(standardTestDispatcher) {
        val component = defaultMainComponent(dispatchers = standartdDispathcers)
        val value = "677"

        component.uiState.test {
            assertEquals("0", awaitItem().preloadCache)
            component.onChangePreloadCache(value)
            assertEquals(value, awaitItem().preloadCache)
        }
    }

    @Test
    fun `On change preload cache when is digit and letters`() = runTest(standardTestDispatcher) {
        val component = defaultMainComponent(dispatchers = standartdDispathcers)
        val value = "1234&fiew"

        component.uiState.test {
            assertEquals("0", awaitItem().preloadCache)
            component.onChangePreloadCache(value)
            expectNoEvents()
        }
    }

    @Test
    fun `onChangeIpv6 updates state`() = runTest(standardTestDispatcher) {
        val component = defaultMainComponent(dispatchers = standartdDispathcers)

        component.uiState.test {
            assertFalse(awaitItem().ipv6)
            component.onChangeIpv6(true)
            assertTrue(awaitItem().ipv6)
        }
    }

    @Test
    fun `onChangeTcp updates state`() = runTest(standardTestDispatcher) {
        val component = defaultMainComponent(dispatchers = standartdDispathcers)

        component.uiState.test {
            assertFalse(awaitItem().tcp)
            component.onChangeTcp(true)
            assertTrue(awaitItem().tcp)
        }
    }

    @Test
    fun `onChangeMtp updates state`() = runTest(standardTestDispatcher) {
        val component = defaultMainComponent(dispatchers = standartdDispathcers)

        component.uiState.test {
            assertFalse(awaitItem().mtp)
            component.onChangeMtp(true)
            assertTrue(awaitItem().mtp)
        }
    }

    @Test
    fun `onChangePex updates state`() = runTest(standardTestDispatcher) {
        val component = defaultMainComponent(dispatchers = standartdDispathcers)

        component.uiState.test {
            assertFalse(awaitItem().pex)
            component.onChangePex(true)
            assertTrue(awaitItem().pex)
        }
    }

    @Test
    fun `onChangeEncryptionHeader updates state`() = runTest(standardTestDispatcher) {
        val component = defaultMainComponent(dispatchers = standartdDispathcers)

        component.uiState.test {
            assertFalse(awaitItem().encryptionHeader)
            component.onChangeEncryptionHeader(true)
            assertTrue(awaitItem().encryptionHeader)
        }
    }

    @Test
    fun `onChangeTimeoutConnection updates state`() = runTest(standardTestDispatcher) {
        val component = defaultMainComponent(dispatchers = standartdDispathcers)

        component.uiState.test {
            assertEquals("0", awaitItem().timeoutConnection)
            component.onChangeTimeoutConnection("30")
            assertEquals("30", awaitItem().timeoutConnection)
        }
    }

    @Test
    fun `onChangeTimeoutConnection does not update state when invalid`() =
        runTest(standardTestDispatcher) {
            val component = defaultMainComponent(dispatchers = standartdDispathcers)

            component.uiState.test {
                assertEquals("0", awaitItem().timeoutConnection)
                component.onChangeTimeoutConnection("abc")
                expectNoEvents()
            }
        }

    @Test
    fun `onChangeTorrentConnections updates state`() = runTest(standardTestDispatcher) {
        val component = defaultMainComponent(dispatchers = standartdDispathcers)

        component.uiState.test {
            assertEquals("0", awaitItem().torrentConnections)
            component.onChangeTorrentConnections("50")
            assertEquals("50", awaitItem().torrentConnections)
        }
    }

    @Test
    fun `onChangeTorrentConnections does not update state when invalid`() =
        runTest(standardTestDispatcher) {
            val component = defaultMainComponent(dispatchers = standartdDispathcers)

            component.uiState.test {
                assertEquals("0", awaitItem().torrentConnections)
                component.onChangeTorrentConnections("xyz")
                expectNoEvents()
            }
        }

    @Test
    fun `onChangeDht updates state`() = runTest(standardTestDispatcher) {
        val component = defaultMainComponent(dispatchers = standartdDispathcers)

        component.uiState.test {
            assertFalse(awaitItem().dht)
            component.onChangeDht(true)
            assertTrue(awaitItem().dht)
        }
    }

    @Test
    fun `onChangeLimitSpeedDownload updates state`() = runTest(standardTestDispatcher) {
        val component = defaultMainComponent(dispatchers = standartdDispathcers)

        component.uiState.test {
            assertEquals("0", awaitItem().limitSpeedDownload)
            component.onChangeLimitSpeedDownload("100")
            assertEquals("100", awaitItem().limitSpeedDownload)
        }
    }

    @Test
    fun `onChangeLimitSpeedDownload does not update state when invalid`() =
        runTest(standardTestDispatcher) {
            val component = defaultMainComponent(dispatchers = standartdDispathcers)

            component.uiState.test {
                assertEquals("0", awaitItem().limitSpeedDownload)
                component.onChangeLimitSpeedDownload("1a2b")
                expectNoEvents()
            }
        }

    @Test
    fun `onChangeIncomingConnection updates state`() = runTest(standardTestDispatcher) {
        val component = defaultMainComponent(dispatchers = standartdDispathcers)

        component.uiState.test {
            assertEquals("0", awaitItem().incomingConnection)
            component.onChangeIncomingConnection("200")
            assertEquals("200", awaitItem().incomingConnection)
        }
    }

    @Test
    fun `onChangeIncomingConnection does not update state when invalid`() =
        runTest(standardTestDispatcher) {
            val component = defaultMainComponent(dispatchers = standartdDispathcers)

            component.uiState.test {
                assertEquals("0", awaitItem().incomingConnection)
                component.onChangeIncomingConnection("test")
                expectNoEvents()
            }
        }

    @Test
    fun `onChangeDistribution updates state`() = runTest(standardTestDispatcher) {
        val component = defaultMainComponent(dispatchers = standartdDispathcers)

        component.uiState.test {
            assertFalse(awaitItem().distribution)
            component.onChangeDistribution(true)
            assertTrue(awaitItem().distribution)
        }
    }

    @Test
    fun `onChangeUpnp updates state`() = runTest(standardTestDispatcher) {
        val component = defaultMainComponent(dispatchers = standartdDispathcers)

        component.uiState.test {
            assertFalse(awaitItem().upnp)
            component.onChangeUpnp(true)
            assertTrue(awaitItem().upnp)
        }
    }

    @Test
    fun `onChangeDlna updates state`() = runTest(standardTestDispatcher) {
        val component = defaultMainComponent(dispatchers = standartdDispathcers)

        component.uiState.test {
            assertFalse(awaitItem().dlna)
            component.onChangeDlna(true)
            assertTrue(awaitItem().dlna)
        }
    }

    @Test
    fun `onChangeLimitSpeedDistribution updates state`() = runTest(standardTestDispatcher) {
        val component = defaultMainComponent(dispatchers = standartdDispathcers)

        component.uiState.test {
            assertEquals("0", awaitItem().limitSpeedDistribution)
            component.onChangeLimitSpeedDistribution("500")
            assertEquals("500", awaitItem().limitSpeedDistribution)
        }
    }

    @Test
    fun `onChangeLimitSpeedDistribution does not update state when invalid`() =
        runTest(standardTestDispatcher) {
            val component = defaultMainComponent(dispatchers = standartdDispathcers)

            component.uiState.test {
                assertEquals("0", awaitItem().limitSpeedDistribution)
                component.onChangeLimitSpeedDistribution("abc123")
                expectNoEvents()
            }
        }

    @Test
    fun `onChangeDlnaName updates state`() = runTest(standardTestDispatcher) {
        val component = defaultMainComponent(dispatchers = standartdDispathcers)

        component.uiState.test {
            assertEquals("", awaitItem().dlnaName)
            component.onChangeDlnaName("MyDLNA")
            assertEquals("MyDLNA", awaitItem().dlnaName)
        }
    }

    @Test
    fun `onChangeLanguage updates state`() = runTest(standardTestDispatcher) {
        val component = defaultMainComponent(dispatchers = standartdDispathcers)

        val language = AppLanguage.RUSSIAN

        component.uiState.test {
            assertEquals(AppLanguage.ENGLISH, awaitItem().language)
            component.onChangeLanguage(language)
            assertEquals(language, awaitItem().language)
        }
    }

    @Test
    fun `Call onClickSave when progress bar is enabled then check error`() =
        runTest(standardTestDispatcher) {
            val component = defaultMainComponent(dispatchers = standartdDispathcers)
            val msg = "Saving in progress"

            coEvery {
                localization.getString(Res.string.main_settings_snackbar_not_available_now)
            } coAnswers {
                msg
            }

            component.uiState.test {
                assertFalse(awaitItem().isShowProgressBar)

                component.showProgressBar()
                component.onClickSave()

                assertTrue(awaitItem().isShowProgressBar)
                assertEquals(msg, awaitItem().snackbar)
            }
        }

    @Test
    fun `onClickSave save settings success`() = runTest(standardTestDispatcher) {
        val component = defaultMainComponent(dispatchers = standartdDispathcers)
        val serverSettings: ServerSettings = mockk(relaxed = true)
        val msg = "Settings saved"

        coEvery {
            serverSettingsApi.saveSettings(any())
        } coAnswers {
            Result.Success(serverSettings)
        }
        coEvery { localization.getString(Res.string.main_settings_snackbar_save) } coAnswers { msg }

        component.uiState.test {
            assertFalse(awaitItem().isShowProgressBar)
            component.onClickSave()
            skipItems(1)
            assertEquals(msg, awaitItem().snackbar)
            assertFalse(awaitItem().isShowProgressBar)
        }
    }

    @Test
    fun `onClickSave save settings failure`() = runTest(standardTestDispatcher) {
        val component = defaultMainComponent(dispatchers = standartdDispathcers)

        coEvery {
            serverSettingsApi.saveSettings(any())
        } coAnswers {
            Result.Error(TorrserverError.HttpError.ResponseReturnNull)
        }

        component.uiState.test {
            assertFalse(awaitItem().isShowProgressBar)
            component.onClickSave()
            skipItems(1)
            assertEquals(
                TorrserverError.HttpError.ResponseReturnNull.toString(),
                awaitItem().snackbar
            )
            assertFalse(awaitItem().isShowProgressBar)
        }
    }

    @Test
    fun `Set new server settings then call onClickSave and check server settings`() =
        runTest(standardTestDispatcher) {
            val component = defaultMainComponent(dispatchers = standartdDispathcers)
            val serverSettingsSlot = slot<ServerSettings>()
            val cacheSize = "1024"
            val readerReadAHead = "64"
            val preloadCache = "128"
            val ipv6 = true
            val tcp = false
            val mtp = true
            val pex = false
            val encryptionHeader = true
            val timeoutConnection = "2"
            val torrentConnections = "10"
            val dht = false
            val upnp = true
            val limitSpeedDownload = "2048"
            val incomingConnection = "24"
            val distribution = true
            val limitSpeedDistribution = "7"
            val dlna = true
            val dlnaName = "New DLNA name"

            coEvery { localization.getString(Res.string.main_settings_snackbar_save) } returns "Settings saved"
            coEvery { serverSettingsApi.saveSettings(capture(serverSettingsSlot)) } answers {
                assertEquals(
                    cacheSize.toLongOrZero().mbToBytes(),
                    serverSettingsSlot.captured.cacheSize
                )
                assertEquals(
                    readerReadAHead.toIntOrZero(),
                    serverSettingsSlot.captured.readerReadAHead
                )
                assertEquals(preloadCache.toIntOrZero(), serverSettingsSlot.captured.preloadCache)
                assertTrue(serverSettingsSlot.captured.ipv6)
                assertFalse(serverSettingsSlot.captured.tcp)
                assertTrue(serverSettingsSlot.captured.mtp)
                assertFalse(serverSettingsSlot.captured.pex)
                assertTrue(serverSettingsSlot.captured.encryptionHeader)
                assertEquals(
                    timeoutConnection.toIntOrZero(),
                    serverSettingsSlot.captured.timeoutConnection
                )
                assertEquals(
                    torrentConnections.toIntOrZero(),
                    serverSettingsSlot.captured.torrentConnections
                )
                assertFalse(serverSettingsSlot.captured.dht)
                assertTrue(serverSettingsSlot.captured.upnp)
                assertEquals(
                    limitSpeedDownload.toIntOrZero(),
                    serverSettingsSlot.captured.limitSpeedDownload
                )
                assertEquals(
                    incomingConnection.toIntOrZero(),
                    serverSettingsSlot.captured.incomingConnection
                )
                assertTrue(serverSettingsSlot.captured.distribution)
                assertEquals(
                    limitSpeedDistribution.toIntOrZero(),
                    serverSettingsSlot.captured.limitSpeedDistribution
                )
                assertTrue(serverSettingsSlot.captured.dlna)
                assertEquals(dlnaName, serverSettingsSlot.captured.dlnaName)

                Result.Success(serverSettingsSlot.captured)
            }

            component.onChangeCacheSize(cacheSize)
            component.onChangeReaderReadAHead(readerReadAHead)
            component.onChangePreloadCache(preloadCache)
            component.onChangeIpv6(ipv6)
            component.onChangeTcp(tcp)
            component.onChangeMtp(mtp)
            component.onChangePex(pex)
            component.onChangeEncryptionHeader(encryptionHeader)
            component.onChangeTimeoutConnection(timeoutConnection)
            component.onChangeTorrentConnections(torrentConnections)
            component.onChangeDht(dht)
            component.onChangeUpnp(upnp)
            component.onChangeLimitSpeedDownload(limitSpeedDownload)
            component.onChangeIncomingConnection(incomingConnection)
            component.onChangeDistribution(distribution)
            component.onChangeLimitSpeedDistribution(limitSpeedDistribution)
            component.onChangeDlna(dlna)
            component.onChangeDlnaName(dlnaName)
            component.onClickSave()
        }

    @Test
    fun `Call dismissSnackbar and check ui state is null`() = runTest(standardTestDispatcher) {
        val component = defaultMainComponent(dispatchers = standartdDispathcers)

        coEvery { serverSettingsApi.saveSettings(any()) } coAnswers {
            Result.Error(TorrserverError.HttpError.ResponseReturnNull)
        }

        assertTrue(component.uiState.value.snackbar.isNullOrEmpty())


        component.uiState.test {
            assertTrue(awaitItem().snackbar.isNullOrEmpty())
            component.onClickSave()
            skipItems(1)
            assertFalse(awaitItem().snackbar.isNullOrEmpty())
            skipItems(1)
            component.dismissSnackbar()
            assertTrue(awaitItem().snackbar.isNullOrEmpty())
        }
    }

    @Test
    fun `Call invokeAction and check new ui state action`() = runTest(standardTestDispatcher) {
        val component = defaultMainComponent(dispatchers = standartdDispathcers)

        component.uiState.test {
            assertEquals(null, awaitItem().action)
            component.invokeAction(MainAction.DefaultSettingsDialog)
            assertEquals(MainAction.DefaultSettingsDialog, awaitItem().action)
        }
    }

    @Test
    fun `Call invokeAction then call dismissAction and check new ui state action`() =
        runTest(standardTestDispatcher) {
            val component = defaultMainComponent(dispatchers = standartdDispathcers)

            component.uiState.test {
                assertEquals(null, awaitItem().action)
                component.invokeAction(MainAction.DefaultSettingsDialog)
                assertEquals(MainAction.DefaultSettingsDialog, awaitItem().action)
                component.dismissAction()
                assertEquals(null, awaitItem().action)
            }
        }

    @Test
    fun `Call defaultSettings when progress bar is enabled then check error`() =
        runTest(standardTestDispatcher) {
            val component = defaultMainComponent(dispatchers = standartdDispathcers)
            val msg = "Not available now"

            coEvery {
                localization.getString(Res.string.main_settings_snackbar_not_available_now)
            } coAnswers {
                msg
            }

            component.uiState.test {
                assertFalse(awaitItem().isShowProgressBar)
                component.showProgressBar()
                component.defaultSettings()
                assertTrue(awaitItem().isShowProgressBar)
                assertEquals(msg, awaitItem().snackbar)
            }
        }

    @Test
    fun `Call defaultSettings with success result and check`() = runTest(standardTestDispatcher) {
        val component = defaultMainComponent(dispatchers = standartdDispathcers)
        val msg = "Default settings applied"

        coEvery {
            localization.getString(Res.string.main_settings_snackbar_default_settings)
        } coAnswers {
            msg
        }
        coEvery { serverSettingsApi.defaultSettings() } coAnswers { Result.Success(mockk(relaxed = true)) }

        component.uiState.test {
            assertFalse(awaitItem().isShowProgressBar)
            component.defaultSettings()
            assertTrue(awaitItem().isShowProgressBar)
            skipItems(1)
            assertEquals(msg, awaitItem().snackbar)
            assertFalse(awaitItem().isShowProgressBar)
        }
    }

    @Test
    fun `Call defaultSettings with error result and check`() = runTest(standardTestDispatcher) {
        val component = defaultMainComponent(dispatchers = standartdDispathcers)

        coEvery { serverSettingsApi.defaultSettings() } coAnswers {
            Result.Error(TorrserverError.HttpError.ResponseReturnNull)
        }

        component.uiState.test {
            assertFalse(awaitItem().isShowProgressBar)
            component.defaultSettings()
            assertTrue(awaitItem().isShowProgressBar)
            assertEquals(
                TorrserverError.HttpError.ResponseReturnNull.toString(),
                awaitItem().snackbar
            )
            assertFalse(awaitItem().isShowProgressBar)
        }
    }

    private fun defaultMainComponent(dispatchers: AppDispatchers = standartdDispathcers) =
        DefaultMainComponent(
            context = mockk(relaxed = true),
            dispatchers = dispatchers,
            serverSettingsApi = serverSettingsApi,
            appSettings = appSettings,
            torrserverApiClient = torrserverApiClient,
            torrserverManager = torrserverManager,
            localization = localization,
            onFinish = onFinish
        )
}
