package com.dik.torrentlist.details

import app.cash.turbine.test
import com.dik.common.AppDispatchers
import com.dik.common.Result
import com.dik.common.i18n.LocalizationResource
import com.dik.torrentlist.converters.bytesToBits
import com.dik.torrentlist.converters.toReadableSize
import com.dik.torrentlist.screens.details.torrentstatistics.DefaultTorrentStatisticsComponent
import com.dik.torrserverapi.TorrserverError
import com.dik.torrserverapi.model.PlayStatistics
import com.dik.torrserverapi.model.Torrent
import com.dik.torrserverapi.server.TorrentApi
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import torrservermedia.features.torrentlist.impl.generated.resources.Res
import torrservermedia.features.torrentlist.impl.generated.resources.main_error_msg_no_server_connection
import kotlin.test.Test
import kotlin.test.assertEquals

class TorrentStatisticsComponentTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    private val unconfiedTestDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(unconfiedTestDispatcher)
    private val dispatchers: AppDispatchers = object : AppDispatchers {
        override fun ioDispatcher() = unconfiedTestDispatcher
        override fun defaultDispatcher() = unconfiedTestDispatcher
        override fun mainDispatcher() = unconfiedTestDispatcher
    }
    private val torrrentApi: TorrentApi = mockk()
    private val localization: LocalizationResource = mockk()

    @Test
    fun `Show success statistiscs for torrent and check in ui state`() = runTest {
        val component = torrentStatisticsComponentTest()
        val statistics: PlayStatistics = mockk(relaxed = true) {
            every { torrentStatus } returns "Ready"
            every { loadedSize } returns 1024
            every { preloadedBytes } returns 64
            every { downloadSpeed } returns 128000.0
            every { uploadSpeed } returns 64000.0
            every { totalPeers } returns 25
            every { activePeers } returns 10
        }
        val torrent = Torrent(
            hash = this.hashCode().toString(),
            title = "At the edge of the abyss",
            poster = "poster",
            name = "name",
            size = 123456789L,
            files = listOf(),
            statistics = statistics
        )

        coEvery { torrrentApi.getTorrent(torrent.hash) } returns Result.Success(torrent)

        component.showStatistics(torrent.hash)

        component.uiState.test {
            val item = awaitItem()

            assertEquals(statistics.torrentStatus, item.torrentStatus)
            assertEquals(statistics.loadedSize.toReadableSize(), item.loadedSize)
            assertEquals(torrent.size.toReadableSize(), item.torrentSize)
            assertEquals(statistics.preloadedBytes.toReadableSize(), item.preloadedBytes)
            assertEquals(statistics.downloadSpeed.bytesToBits(), item.downloadSpeed)
            assertEquals(statistics.uploadSpeed.bytesToBits(), item.uploadSpeed)
            assertEquals(statistics.totalPeers.toString(), item.totalPeers)
            assertEquals(statistics.activePeers.toString(), item.activePeers)
        }
    }

    @Test
    fun `Show failure statistiscs for torrent and check in ui state`() = runTest {
        val component = torrentStatisticsComponentTest()

        coEvery { torrrentApi.getTorrent(any()) } returns Result.Error(TorrserverError.Server.NoServerConnection)
        coEvery { localization.getString(Res.string.main_error_msg_no_server_connection) } returns "No server connection"

        component.showStatistics("hash_code")

        component.uiState.test {
            val item = awaitItem()

            assertEquals("No server connection",  item.error)
        }
    }

    private fun torrentStatisticsComponentTest() = DefaultTorrentStatisticsComponent(
        componentContext = mockk(relaxed = true),
        dispatchers = dispatchers,
        componentScope = testScope,
        torrrentApi = torrrentApi,
        localization = localization
    )
}