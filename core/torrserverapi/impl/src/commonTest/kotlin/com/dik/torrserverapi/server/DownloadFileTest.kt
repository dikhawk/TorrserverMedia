package com.dik.torrserverapi.server

import app.cash.turbine.test
import com.dik.common.AppDispatchers
import com.dik.common.ResultProgress
import com.dik.torrserverapi.TorrserverError
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondError
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.utils.io.ByteReadChannel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import okio.FileSystem
import okio.IOException
import okio.Path.Companion.toPath
import okio.fakefilesystem.FakeFileSystem
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DownloadFileTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    private val testDispatcher = UnconfinedTestDispatcher()
    private val appDispatchers: AppDispatchers = object : AppDispatchers {
        override fun ioDispatcher(): CoroutineDispatcher = testDispatcher
        override fun defaultDispatcher(): CoroutineDispatcher = testDispatcher
        override fun mainDispatcher(): CoroutineDispatcher = testDispatcher
    }
    private val testScope = TestScope(testDispatcher)

    @Test
    fun `Download file success`() = testScope.runTest {
        val fileUrl = "https://example.com/file.txt"
        val mockEngine = MockEngine { request ->
            when (request.url.toString()) {
                fileUrl -> {
                    val content = "Hello, world!"
                    respond(
                        content = ByteReadChannel(content.toByteArray()),
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentLength, content.length.toString())
                    )
                }
                else -> respondError(HttpStatusCode.NotFound)
            }
        }
        val ktor = HttpClient(mockEngine)
        val outputPath = "downloaded.txt"
        val outputPathOkio = outputPath.toPath()
        val fileSystem = FakeFileSystem()
        val downloadFile = DownloadFile(dispatchers = appDispatchers, fileSystem = fileSystem, ktor = ktor)

        downloadFile.invoke(fileUrl, outputPath).test {
            assertTrue(awaitItem() is ResultProgress.Loading)

            val progress = awaitItem()
            assertTrue(progress is ResultProgress.Loading)
            assertEquals(100.0, progress.progress.progress)

            val result = awaitItem()
            assertTrue(result is ResultProgress.Success)
            assertTrue(fileSystem.metadata(outputPathOkio).size!! > 0L)
            assertEquals(outputPath, result.data.filePath)

            cancelAndIgnoreRemainingEvents()

            assertTrue(fileSystem.exists(outputPath.toPath()))
            assertEquals("Hello, world!", fileSystem.read(outputPath.toPath()) { readUtf8() })
        }
    }

    @Test
    fun `Download file failed with 404 error`() = testScope.runTest {
        val fileUrl = "https://example.com/file.txt"
        val mockEngine = MockEngine { request ->
            when (request.url.toString()) {
                fileUrl -> {
                    respond(
                        content = "File not found",
                        status = HttpStatusCode.NotFound,
                    )
                }
                else -> throw IllegalStateException()
            }
        }
        val ktor = HttpClient(mockEngine)
        val outputPath = "downloaded.txt"
        val fileSystem = FakeFileSystem()
        val downloadFile = DownloadFile(dispatchers = appDispatchers, fileSystem = fileSystem, ktor = ktor)

        downloadFile.invoke(fileUrl, outputPath).test {
            assertTrue(awaitItem() is ResultProgress.Loading)

            val result = awaitItem()

            assertTrue(result is ResultProgress.Error)

            awaitComplete()
        }
    }

    @Test
    fun `Download file with no access to read in file system`() = testScope.runTest {
        val fileUrl = "https://example.com/file.txt"
        val mockEngine = MockEngine { request ->
            when (request.url.toString()) {
                fileUrl -> {
                    val content = "Hello, world!"
                    respond(
                        content = ByteReadChannel(content.toByteArray()),
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentLength, content.length.toString())
                    )
                }
                else -> respondError(HttpStatusCode.NotFound)
            }
        }
        val ktor = HttpClient(mockEngine)
        val outputPath = "downloaded.txt"
        val fileSystem: FileSystem = mockk(relaxed = true) {
            every { createDirectories(any()) } throws IOException("No access to read")
        }
        val downloadFile = DownloadFile(dispatchers = appDispatchers, fileSystem = fileSystem, ktor = ktor)

        downloadFile.invoke(fileUrl, outputPath).test {
            assertTrue(awaitItem() is ResultProgress.Loading)

            val error = awaitItem()
            assertTrue(error is ResultProgress.Error)
            assertEquals("java.io.IOException: No access to read", (error.error as TorrserverError.Unknown).message)
            awaitComplete()
        }
    }
}