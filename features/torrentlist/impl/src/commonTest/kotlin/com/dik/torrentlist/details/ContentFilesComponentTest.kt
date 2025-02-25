package com.dik.torrentlist.details

import app.cash.turbine.test
import com.dik.common.AppDispatchers
import com.dik.torrentlist.screens.details.files.DefaultContentFilesComponent
import com.dik.torrserverapi.ContentFile
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ContentFilesComponentTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    private val unconfiedTestDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(unconfiedTestDispatcher)
    private val dispatchers: AppDispatchers = object : AppDispatchers {
        override fun ioDispatcher() = unconfiedTestDispatcher
        override fun defaultDispatcher() = unconfiedTestDispatcher
        override fun mainDispatcher() = unconfiedTestDispatcher
    }
    private val onClickPlayFile: suspend (contentFile: ContentFile) -> Unit = mockk(relaxed = true)

    @Test
    fun `Show files then check ui state files`() = runTest {
        val component = contentFilesComponent()
        val contentFile1 = ContentFile(
            id = this.hashCode(),
            path = "Season 1/At the edge of the abyss.S1E1.1080p.rus.mkv",
            length = 123456789L,
            url = "url_to",
            isViewed = false
        )
        val contentFile2 = ContentFile(
            id = this.hashCode(),
            path = "Season 2/At the edge of the abyss.S2E1.1080p.rus.mkv",
            length = 123456789L,
            url = "url_to",
            isViewed = false
        )
        val contentFile3 = ContentFile(
            id = this.hashCode(),
            path = "Season 2/At the edge of the abyss.S2E2.1080p.rus.mkv",
            length = 123456789L,
            url = "url_to",
            isViewed = false
        )

        component.showFiles(listOf(contentFile1, contentFile2, contentFile3))

        component.uiState.test {
            val item = awaitItem()
            assertTrue(item.files.containsKey("Season 1"))
            assertTrue(item.files.containsKey("Season 2"))
            assertEquals(1, item.files["Season 1"]!!.size)
            assertEquals(2, item.files["Season 2"]!!.size)
        }
    }

    @Test
    fun `On click item and check onClickPlayFile is called`() = runTest {
        val component = contentFilesComponent()
        val contentFile = ContentFile(
            id = this.hashCode(),
            path = "Season 1/At the edge of the abyss.S1E1.1080p.rus.mkv",
            length = 123456789L,
            url = "url_to",
            isViewed = false
        )

        component.onClickItem(contentFile)

        coVerify { onClickPlayFile.invoke(contentFile) }
    }

    private fun contentFilesComponent() = DefaultContentFilesComponent(
        componentContext = mockk(relaxed = true),
        dispatchers = dispatchers,
        componentScope = testScope,
        onClickPlayFile = onClickPlayFile
    )
}