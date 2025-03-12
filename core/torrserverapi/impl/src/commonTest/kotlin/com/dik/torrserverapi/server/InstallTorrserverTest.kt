package com.dik.torrserverapi.server

import app.cash.turbine.test
import com.dik.common.AppDispatchers
import com.dik.common.Result
import com.dik.common.ResultProgress
import com.dik.torrserverapi.TorrserverError
import com.dik.torrserverapi.model.Asset
import com.dik.torrserverapi.model.Release
import com.dik.torrserverapi.model.TorrserverFile
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class InstallTorrserverTest {

    private val torrserverStuffApi: TorrserverStuffApi = mockk(relaxed = true)
    private val downloadFile: DownloadFile = mockk(relaxed = true)
    private val backupFile: BackupFile = mockk(relaxed = true)
    private val restoreServerFromBackUp: RestoreServerFromBackUp = mockk(relaxed = true)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val testDispatcher = UnconfinedTestDispatcher()
    private val dispatchers: AppDispatchers = object : AppDispatchers {
        override fun ioDispatcher(): CoroutineDispatcher = testDispatcher
        override fun defaultDispatcher(): CoroutineDispatcher = testDispatcher
        override fun mainDispatcher(): CoroutineDispatcher = testDispatcher
    }

    @Test
    fun `Install Torrserver successful`() = runTest {
        val downloadFileState: StateFlow<ResultProgress<TorrserverFile, TorrserverError>> =
            MutableStateFlow(ResultProgress.Success(mockk(relaxed = true)))
        val installTorrserver = getInstallTorrserver()
        val outputFilePath = "torrserver_file"
        val outputBackupFilePath = "backup_file"
        val release = Release(
            url = "https://example.com",
            tagName = "main",
            publishedAt = "2023-06-07T15:54",
            assets = listOf(
                Asset(
                    name = "torrserver-unknown_android_windows_linux",
                    browserDownloadUrl = "https://example.com/download",
                    updatedAt = "2023-06-07T15:54"
                )
            )
        )
        coEvery { torrserverStuffApi.checkLatestRelease() } returns Result.Success(release)
        coEvery { downloadFile.invoke(any(), any()) } returns downloadFileState

        installTorrserver(outputFilePath, outputBackupFilePath).test {
            assertTrue(awaitItem() is ResultProgress.Success)
        }
    }

    @Test
    fun `Cpu arch not supported check error`() = runTest {
        val downloadFileState: StateFlow<ResultProgress<TorrserverFile, TorrserverError>> =
            MutableStateFlow(ResultProgress.Success(mockk(relaxed = true)))
        val installTorrserver = getInstallTorrserver()
        val outputFilePath = "torrserver_file"
        val outputBackupFilePath = "backup_file"
        val release = Release(
            url = "https://example.com",
            tagName = "main",
            publishedAt = "2023-06-07T15:54",
            assets = listOf(
                Asset(
                    name = "torrserver_x86_android_windows_linux",
                    browserDownloadUrl = "https://example.com/download",
                    updatedAt = "2023-06-07T15:54"
                )
            )
        )

        coEvery { torrserverStuffApi.checkLatestRelease() } returns Result.Success(release)
        coEvery { downloadFile.invoke(any(), any()) } returns downloadFileState

        installTorrserver(outputFilePath, outputBackupFilePath).test {
            val result = awaitItem()

            assertTrue(result is ResultProgress.Error)
            assertTrue(result.error is TorrserverError.Server.PlatformNotSupported)

            awaitComplete()
        }
    }

    @Test
    fun `checkLatestRelease return error`() = runTest {
        val installTorrserver = getInstallTorrserver()
        val outputFilePath = "torrserver_file"
        val outputBackupFilePath = "backup_file"

        coEvery { torrserverStuffApi.checkLatestRelease() } returns
                Result.Error(TorrserverError.HttpError.ResponseReturnError("Response return error 404"))

        installTorrserver(outputFilePath, outputBackupFilePath).test {
            val result = awaitItem()
            assertTrue(result is ResultProgress.Error)
            assertTrue(result.error is TorrserverError.HttpError.ResponseReturnError)
            awaitComplete()
        }
    }

    @Test
    fun `If have error restore backup from file`() = runTest {
        val downloadFileState: StateFlow<ResultProgress<TorrserverFile, TorrserverError>> =
            MutableStateFlow(ResultProgress.Error(TorrserverError.HttpError.ResponseReturnNull))
        val installTorrserver = getInstallTorrserver()
        val outputFilePath = "torrserver_file"
        val outputBackupFilePath = "backup_file"
        val release = Release(
            url = "https://example.com",
            tagName = "main",
            publishedAt = "2023-06-07T15:54",
            assets = listOf(
                Asset(
                    name = "torrserver_unknown_android_windows_linux",
                    browserDownloadUrl = "https://example.com/download",
                    updatedAt = "2023-06-07T15:54"
                )
            )
        )

        coEvery { torrserverStuffApi.checkLatestRelease() } returns Result.Success(release)
        coEvery { downloadFile.invoke(any(), any()) } returns downloadFileState

        installTorrserver(outputFilePath, outputBackupFilePath).test {
            awaitItem()
            coVerify (exactly = 1) { restoreServerFromBackUp.invoke(any(), any()) }

        }
    }

    private fun getInstallTorrserver() = InstallTorrserver(
        torrserverStuffApi = torrserverStuffApi,
        downloadFile = downloadFile,
        backupFile = backupFile,
        restoreServerFromBackUp = restoreServerFromBackUp,
        dispatchers = dispatchers
    )
}