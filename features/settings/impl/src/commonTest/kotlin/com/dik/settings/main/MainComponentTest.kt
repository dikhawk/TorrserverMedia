package com.dik.settings.main

import app.cash.turbine.test
import com.dik.appsettings.api.model.AppSettings
import com.dik.common.AppDispatchers
import com.dik.common.Progress
import com.dik.common.Result
import com.dik.common.ResultProgress
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
import com.dik.torrserverapi.model.TorrserverFile
import com.dik.torrserverapi.server.ServerSettingsApi
import com.dik.torrserverapi.server.TorrserverCommands
import com.dik.torrserverapi.server.TorrserverStuffApi
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
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
    private val testScope = TestScope(standardTestDispatcher)
    private val standartdDispathcer = object : AppDispatchers {
        override fun ioDispatcher() = standardTestDispatcher
        override fun defaultDispatcher() = standardTestDispatcher
        override fun mainDispatcher() = standardTestDispatcher
    }
    private val appSettings: AppSettings = mockk(relaxed = true) {
        every { defaultPlayer } returns Player.VLC
        every { language } returns AppLanguage.ENGLISH
    }
    private val torrserverStuffApi: TorrserverStuffApi = mockk()
    private val torrserverCommands: TorrserverCommands = mockk()
    private val localization: LocalizationResource = mockk()
    private val onFinish: () -> Unit = mockk(relaxed = true)


    @Test
    fun `Load Settings success and check ui state`() = testScope.runTest {
        val component = defaultMainComponent()
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
        coEvery { serverSettingsApi.getSettings() } returns Result.Success(serverSettings)
        coEvery { torrserverStuffApi.echo() } returns Result.Success("Torrserver")
        coEvery { torrserverCommands.isAvailableNewVersion() } returns Result.Success(false)

        component.loadSettings()

        component.uiState.test {
            val itemBefore = awaitItem()

            assertTrue(itemBefore.isShowProgressBar)

            val item = awaitItem()
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
    fun `Load Settings failure and check ui state`() = testScope.runTest {
        val component = defaultMainComponent()

        coEvery { serverSettingsApi.getSettings() } returns Result.Error(TorrserverError.HttpError.ResponseReturnNull)
        coEvery { torrserverStuffApi.echo() } returns Result.Success("Torrserver")
        coEvery { torrserverCommands.isAvailableNewVersion() } returns Result.Success(false)

        component.loadSettings()

        component.uiState.test {
            assertTrue(awaitItem().isShowProgressBar)
            val item = awaitItem()

            assertEquals(TorrserverError.HttpError.ResponseReturnNull.toString(), item.snackbar)
            assertFalse(awaitItem().isShowProgressBar)
        }
    }

    @Test
    fun `Load settings and check updates when available new version`() = testScope.runTest {
        val component = defaultMainComponent()

        val isAvailableNewVersion = "Is available new version"
        coEvery { serverSettingsApi.getSettings() } returns Result.Success(mockk(relaxed = true))
        coEvery { torrserverStuffApi.echo() } returns Result.Success("Torrserver")
        coEvery { torrserverCommands.isAvailableNewVersion() } returns Result.Success(true)
        coEvery { localization.getString(Res.string.main_settings_available_new_version) } returns isAvailableNewVersion

        component.loadSettings()

        component.uiState.test {
            skipItems(2)
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
    fun `On click update torrserver loading`() = testScope.runTest {
        val installServerState: MutableStateFlow<ResultProgress<TorrserverFile, TorrserverError>> =
            MutableStateFlow(
                ResultProgress.Loading(
                    Progress(progress = 0.35, totalBytes = 1024, currentBytes = 64)
                )
            )
        val updateProgressMsg = "Update torrserver progress %s %%"
        val component = defaultMainComponent()

        coEvery { torrserverStuffApi.echo() } returns Result.Success("Torrserver")
        coEvery { torrserverCommands.installServer() } returns installServerState
        coEvery { localization.getString(Res.string.main_settings_available_new_version_update_loading) } returns updateProgressMsg
        coEvery { serverSettingsApi.getSettings() } returns Result.Success(mockk(relaxed = true))
        coEvery { torrserverCommands.isAvailableNewVersion() } returns Result.Success(false)

        component.onClickUpdateTorrserver()

        component.uiState.test {
            val beforeStartLoading = awaitItem()

            assertTrue(beforeStartLoading.isShowAvailableNewVersionProgress)

            val afterStartLoading = awaitItem()

            assertEquals(updateProgressMsg.format(0.35.toString()), afterStartLoading.availableNewVersionText)
        }
    }

    @Test
    fun `On click update torrserver error`() = testScope.runTest {
        val installServerState: MutableStateFlow<ResultProgress<TorrserverFile, TorrserverError>> =
            MutableStateFlow(ResultProgress.Error(TorrserverError.HttpError.ResponseReturnNull))
        val updateErrorMsg = "Update error"
        val component = defaultMainComponent()

        coEvery { torrserverStuffApi.echo() } returns Result.Success("Torrserver")
        coEvery { torrserverCommands.installServer() } returns installServerState
        coEvery { localization.getString(Res.string.main_settings_available_new_version_update_error) } returns updateErrorMsg
        coEvery { serverSettingsApi.getSettings() } returns Result.Success(mockk(relaxed = true))
        coEvery { torrserverCommands.isAvailableNewVersion() } returns Result.Success(false)

        component.onClickUpdateTorrserver()

        component.uiState.test {
            val beforeStartLoading = awaitItem()

            assertTrue(beforeStartLoading.isShowAvailableNewVersionProgress)

            val item = awaitItem()
            assertEquals(updateErrorMsg, item.availableNewVersionText)
            assertFalse(item.isShowAvailableNewVersionProgress)
        }
    }

    @Test
    fun `On click update torrserver success`() = testScope.runTest {
        val installServerState: MutableStateFlow<ResultProgress<TorrserverFile, TorrserverError>> =
            MutableStateFlow(ResultProgress.Success(mockk(relaxed = true)))
        val updateSuccess = "Update is success"
        val component = defaultMainComponent()

        coEvery { torrserverStuffApi.echo() } returns Result.Success("Torrserver")
        coEvery { torrserverCommands.installServer() } returns installServerState
        coEvery { localization.getString(Res.string.main_settings_available_new_version_update_success) } returns updateSuccess
        coEvery { serverSettingsApi.getSettings() } returns Result.Success(mockk(relaxed = true))
        coEvery { torrserverCommands.isAvailableNewVersion() } returns Result.Success(false)
        coEvery { torrserverCommands.stopServer() } returns Result.Success(mockk(relaxed = true))
        coEvery { torrserverCommands.startServer() } returns Result.Success(mockk(relaxed = true))

        component.onClickUpdateTorrserver()

        component.uiState.test {
            val beforeStartLoading = awaitItem()

            assertTrue(beforeStartLoading.isShowAvailableNewVersionProgress)

            val item = awaitItem()
            assertEquals(updateSuccess, item.availableNewVersionText)
            assertFalse(item.isAvailableNewVersion)
            assertFalse(item.isShowAvailableNewVersionProgress)
        }

        coVerify { torrserverCommands.stopServer() }
        coVerify { torrserverCommands.startServer() }
    }

    @Test
    fun `On change default player`() = runTest {
        val component = defaultMainComponent()
        val players = Player.entries
        val randomPlayer = players[Random.nextInt(0, players.size - 1)]

        assertEquals(Player.DEFAULT_PLAYER, component.uiState.value.player)

        component.onChangeDefaultPlayer(randomPlayer)

        component.uiState.test {
            assertEquals(randomPlayer, awaitItem().player)
        }
    }

    @Test
    fun `On change cache size when is only digit`() = runTest {
        val component = defaultMainComponent()
        val value = "1234"

        assertEquals("0", component.uiState.value.cacheSize)

        component.onChangeCacheSize(value)

        component.uiState.test {
            assertEquals(value, awaitItem().cacheSize)
        }
    }

    @Test
    fun `On change cache size when is digit and letters`() = runTest {
        val component = defaultMainComponent()
        val value = "1234fiew"

        component.onChangeCacheSize(value)

        assertEquals("0", component.uiState.value.cacheSize)

        component.uiState.test {
            assertEquals("0", awaitItem().cacheSize)
        }
    }

    @Test
    fun `On change reader read a head when is only digit`() = runTest {
        val component = defaultMainComponent()
        val value = "677"

        assertEquals("0", component.uiState.value.readerReadAHead)

        component.onChangeReaderReadAHead(value)

        component.uiState.test {
            assertEquals(value, awaitItem().readerReadAHead)
        }
    }

    @Test
    fun `On change reader read a head when is digit and letters`() = runTest {
        val component = defaultMainComponent()
        val value = "1234fiew"

        component.onChangeReaderReadAHead(value)

        assertEquals("0", component.uiState.value.readerReadAHead)

        component.uiState.test {
            assertEquals("0", awaitItem().readerReadAHead)
        }
    }

    @Test
    fun `On change preload cache when is only digit`() = runTest {
        val component = defaultMainComponent()
        val value = "677"

        assertEquals("0", component.uiState.value.preloadCache)

        component.onChangePreloadCache(value)

        component.uiState.test {
            assertEquals(value, awaitItem().preloadCache)
        }
    }

    @Test
    fun `On change preload cache when is digit and letters`() = runTest {
        val component = defaultMainComponent()
        val value = "1234&fiew"

        component.onChangePreloadCache(value)

        assertEquals("0", component.uiState.value.preloadCache)

        component.uiState.test {
            assertEquals("0", awaitItem().preloadCache)
        }
    }

    @Test
    fun `onChangeIpv6 updates state`() = runTest {
        val component = defaultMainComponent()
        component.onChangeIpv6(true)
        component.uiState.test {
            assertEquals(true, awaitItem().ipv6)
        }
    }

    @Test
    fun `onChangeTcp updates state`() = runTest {
        val component = defaultMainComponent()
        component.onChangeTcp(true)
        component.uiState.test {
            assertEquals(true, awaitItem().tcp)
        }
    }

    @Test
    fun `onChangeMtp updates state`() = runTest {
        val component = defaultMainComponent()
        component.onChangeMtp(true)
        component.uiState.test {
            assertEquals(true, awaitItem().mtp)
        }
    }

    @Test
    fun `onChangePex updates state`() = runTest {
        val component = defaultMainComponent()
        component.onChangePex(true)
        component.uiState.test {
            assertEquals(true, awaitItem().pex)
        }
    }

    @Test
    fun `onChangeEncryptionHeader updates state`() = runTest {
        val component = defaultMainComponent()
        component.onChangeEncryptionHeader(true)
        component.uiState.test {
            assertEquals(true, awaitItem().encryptionHeader)
        }
    }

    @Test
    fun `onChangeTimeoutConnection updates state`() = runTest {
        val component = defaultMainComponent()
        component.onChangeTimeoutConnection("30")
        component.uiState.test {
            assertEquals("30", awaitItem().timeoutConnection)
        }
    }

    @Test
    fun `onChangeTimeoutConnection does not update state when invalid`() = runTest {
        val component = defaultMainComponent()
        component.onChangeTimeoutConnection("abc")
        component.uiState.test {
            assertEquals("0", awaitItem().timeoutConnection)
        }
    }

    @Test
    fun `onChangeTorrentConnections updates state`() = runTest {
        val component = defaultMainComponent()
        component.onChangeTorrentConnections("50")
        component.uiState.test {
            assertEquals("50", awaitItem().torrentConnections)
        }
    }

    @Test
    fun `onChangeTorrentConnections does not update state when invalid`() = runTest {
        val component = defaultMainComponent()
        component.onChangeTorrentConnections("xyz")
        component.uiState.test {
            assertEquals("0", awaitItem().torrentConnections)
        }
    }

    @Test
    fun `onChangeDht updates state`() = runTest {
        val component = defaultMainComponent()
        component.onChangeDht(true)
        component.uiState.test {
            assertEquals(true, awaitItem().dht)
        }
    }

    @Test
    fun `onChangeLimitSpeedDownload updates state`() = runTest {
        val component = defaultMainComponent()
        component.onChangeLimitSpeedDownload("100")
        component.uiState.test {
            assertEquals("100", awaitItem().limitSpeedDownload)
        }
    }

    @Test
    fun `onChangeLimitSpeedDownload does not update state when invalid`() = runTest {
        val component = defaultMainComponent()
        component.onChangeLimitSpeedDownload("1a2b")
        component.uiState.test {
            assertEquals("0", awaitItem().limitSpeedDownload)
        }
    }

    @Test
    fun `onChangeIncomingConnection updates state`() = runTest {
        val component = defaultMainComponent()
        component.onChangeIncomingConnection("200")
        component.uiState.test {
            assertEquals("200", awaitItem().incomingConnection)
        }
    }

    @Test
    fun `onChangeIncomingConnection does not update state when invalid`() = runTest {
        val component = defaultMainComponent()
        component.onChangeIncomingConnection("test")
        component.uiState.test {
            assertEquals("0", awaitItem().incomingConnection)
        }
    }

    @Test
    fun `onChangeDistribution updates state`() = runTest {
        val component = defaultMainComponent()
        component.onChangeDistribution(true)
        component.uiState.test {
            assertEquals(true, awaitItem().distribution)
        }
    }

    @Test
    fun `onChangeUpnp updates state`() = runTest {
        val component = defaultMainComponent()
        component.onChangeUpnp(true)
        component.uiState.test {
            assertEquals(true, awaitItem().upnp)
        }
    }

    @Test
    fun `onChangeDlna updates state`() = runTest {
        val component = defaultMainComponent()
        component.onChangeDlna(true)
        component.uiState.test {
            assertEquals(true, awaitItem().dlna)
        }
    }

    @Test
    fun `onChangeLimitSpeedDistribution updates state`() = runTest {
        val component = defaultMainComponent()
        component.onChangeLimitSpeedDistribution("500")
        component.uiState.test {
            assertEquals("500", awaitItem().limitSpeedDistribution)
        }
    }

    @Test
    fun `onChangeLimitSpeedDistribution does not update state when invalid`() = runTest {
        val component = defaultMainComponent()
        component.onChangeLimitSpeedDistribution("abc123")
        component.uiState.test {
            assertEquals("0", awaitItem().limitSpeedDistribution)
        }
    }

    @Test
    fun `onChangeDlnaName updates state`() = runTest {
        val component = defaultMainComponent()
        component.onChangeDlnaName("MyDLNA")
        component.uiState.test {
            assertEquals("MyDLNA", awaitItem().dlnaName)
        }
    }

    @Test
    fun `onChangeLanguage updates state`() = runTest {
        val component = defaultMainComponent()
        val language = AppLanguage.ENGLISH
        component.onChangeLanguage(language)
        component.uiState.test {
            assertEquals(language, awaitItem().language)
        }
    }

    @Test
    fun `Call onClickSave when progress bar is enabled then check error`() = testScope.runTest {
        val component = defaultMainComponent()
        val msg = "Saving in progress"

        coEvery { localization.getString(Res.string.main_settings_snackbar_not_available_now) } returns msg

        assertFalse(component.uiState.value.isShowProgressBar)

        component.showProgressBar()
        component.onClickSave()


        component.uiState.test {
            assertTrue(awaitItem().isShowProgressBar)
            assertEquals(msg, awaitItem().snackbar)
        }
    }

    @Test
    fun `onClickSave save settings success`() = testScope.runTest {
        val component = defaultMainComponent()
        val serverSettings: ServerSettings = mockk(relaxed = true)
        val msg = "Settings saved"

        coEvery { serverSettingsApi.saveSettings(any()) } returns Result.Success(serverSettings)
        coEvery { localization.getString(Res.string.main_settings_snackbar_save) } returns msg

        component.onClickSave()

        component.uiState.test {
            assertTrue(awaitItem().isShowProgressBar)
            assertEquals(msg, awaitItem().snackbar)
            assertFalse(awaitItem().isShowProgressBar)
        }
    }

    @Test
    fun `onClickSave save settings failure`() = testScope.runTest {
        val component = defaultMainComponent()

        coEvery { serverSettingsApi.saveSettings(any()) } returns
                Result.Error(TorrserverError.HttpError.ResponseReturnNull)

        component.onClickSave()

        component.uiState.test {
            assertTrue(awaitItem().isShowProgressBar)
            assertEquals(TorrserverError.HttpError.ResponseReturnNull.toString(), awaitItem().snackbar)
            assertFalse(awaitItem().isShowProgressBar)
        }
    }

    @Test
    fun `Set new server settings then call onClickSave and check server settings`() = testScope.runTest {
        val component = defaultMainComponent()
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
            assertEquals(cacheSize.toLongOrZero().mbToBytes(), serverSettingsSlot.captured.cacheSize)
            assertEquals(readerReadAHead.toIntOrZero(), serverSettingsSlot.captured.readerReadAHead)
            assertEquals(preloadCache.toIntOrZero(), serverSettingsSlot.captured.preloadCache)
            assertTrue(serverSettingsSlot.captured.ipv6)
            assertFalse(serverSettingsSlot.captured.tcp)
            assertTrue(serverSettingsSlot.captured.mtp)
            assertFalse(serverSettingsSlot.captured.pex)
            assertTrue(serverSettingsSlot.captured.encryptionHeader)
            assertEquals(timeoutConnection.toIntOrZero(), serverSettingsSlot.captured.timeoutConnection)
            assertEquals(torrentConnections.toIntOrZero(), serverSettingsSlot.captured.torrentConnections)
            assertFalse(serverSettingsSlot.captured.dht)
            assertTrue(serverSettingsSlot.captured.upnp)
            assertEquals(limitSpeedDownload.toIntOrZero(), serverSettingsSlot.captured.limitSpeedDownload)
            assertEquals(incomingConnection.toIntOrZero(), serverSettingsSlot.captured.incomingConnection)
            assertTrue(serverSettingsSlot.captured.distribution)
            assertEquals(limitSpeedDistribution.toIntOrZero(), serverSettingsSlot.captured.limitSpeedDistribution)
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
    fun `Call dismissSnackbar and check ui state is null`() = testScope.runTest {
        val component = defaultMainComponent()

        coEvery { serverSettingsApi.saveSettings(any()) } returns
                Result.Error(TorrserverError.HttpError.ResponseReturnNull)

        assertTrue(component.uiState.value.snackbar.isNullOrEmpty())

        component.onClickSave()

        component.uiState.test {
            skipItems(1)
            assertFalse(awaitItem().snackbar.isNullOrEmpty())
            skipItems(1)
            component.dismissSnackbar()
            assertTrue(awaitItem().snackbar.isNullOrEmpty())
        }
    }

    @Test
    fun `Call invokeAction and check new ui state action`() = testScope.runTest {
        val component = defaultMainComponent()

        assertEquals(null, component.uiState.value.action)

        component.invokeAction(MainAction.DefaultSettingsDialog)

        component.uiState.test {
            assertEquals(MainAction.DefaultSettingsDialog, awaitItem().action)
        }
    }

    @Test
    fun `Call invokeAction then call dismissAction and check new ui state action`() = testScope.runTest {
        val component = defaultMainComponent()

        assertEquals(null, component.uiState.value.action)

        component.invokeAction(MainAction.DefaultSettingsDialog)

        component.uiState.test {
            assertEquals(MainAction.DefaultSettingsDialog, awaitItem().action)
            component.dismissAction()
            assertEquals(null, awaitItem().action)
        }
    }

    @Test
    fun `Call defaultSettings when progress bar is enabled then check error`() = testScope.runTest {
        val component = defaultMainComponent()
        val msg = "Not available now"

        coEvery { localization.getString(Res.string.main_settings_snackbar_not_available_now) } returns msg

        assertFalse(component.uiState.value.isShowProgressBar)
        component.showProgressBar()
        component.defaultSettings()

        component.uiState.test {
            assertTrue(awaitItem().isShowProgressBar)
            assertEquals(msg, awaitItem().snackbar)
        }
    }

    @Test
    fun `Call defaultSettings with success result and check`() = testScope.runTest {
        val component = defaultMainComponent()
        val msg = "Default settings applied"

        coEvery { localization.getString(Res.string.main_settings_snackbar_default_settings) } returns msg
        coEvery { serverSettingsApi.defaultSettings() } returns Result.Success(mockk(relaxed = true))

        assertFalse(component.uiState.value.isShowProgressBar)
        component.defaultSettings()

        component.uiState.test {
            assertTrue(awaitItem().isShowProgressBar)
            skipItems(1)
            assertEquals(msg, awaitItem().snackbar)
            assertFalse(awaitItem().isShowProgressBar)
        }
    }

    @Test
    fun `Call defaultSettings with error result and check`() = testScope.runTest {
        val component = defaultMainComponent()

        coEvery { serverSettingsApi.defaultSettings() } returns
                Result.Error(TorrserverError.HttpError.ResponseReturnNull)

        assertFalse(component.uiState.value.isShowProgressBar)
        component.defaultSettings()

        component.uiState.test {
            assertTrue(awaitItem().isShowProgressBar)
            assertEquals(TorrserverError.HttpError.ResponseReturnNull.toString(), awaitItem().snackbar)
            assertFalse(awaitItem().isShowProgressBar)
        }
    }

    private fun defaultMainComponent(dispatchers: AppDispatchers = standartdDispathcer) = DefaultMainComponent(
        context = mockk(relaxed = true),
        dispatchers = dispatchers,
        serverSettingsApi = serverSettingsApi,
        appSettings = appSettings,
        torrserverStuffApi = torrserverStuffApi,
        torrserverCommands = torrserverCommands,
        localization = localization,
        onFinish = onFinish
    )
}