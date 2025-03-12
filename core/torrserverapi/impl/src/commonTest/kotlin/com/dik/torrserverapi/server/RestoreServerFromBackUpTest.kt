package com.dik.torrserverapi.server

import com.dik.common.AppDispatchers
import com.dik.common.Result
import com.dik.torrserverapi.TorrserverError
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import okio.FileSystem
import okio.IOException
import okio.Path.Companion.toPath
import okio.fakefilesystem.FakeFileSystem
import org.junit.Test
import kotlin.test.assertTrue

class RestoreServerFromBackUpTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    private val testDispatcher = UnconfinedTestDispatcher()
    private val dispatchers: AppDispatchers = object : AppDispatchers {
        override fun ioDispatcher(): CoroutineDispatcher = testDispatcher
        override fun defaultDispatcher(): CoroutineDispatcher = testDispatcher
        override fun mainDispatcher(): CoroutineDispatcher = testDispatcher
    }


    @Test
    fun `Create backup success result`() = runTest {
        val fileSystem = FakeFileSystem()
        val pathToBackupFile = "backup_file"
        val pathToFile = "restored_backup_file"
        val restoreServerFromBackUp = RestoreServerFromBackUp(dispatchers = dispatchers, fileSystem = fileSystem)

        fileSystem.write(pathToBackupFile.toPath()) { writeUtf8("original file") }

        val result = restoreServerFromBackUp.invoke(pathToBackupFile = pathToBackupFile, pathToFile = pathToFile)

        assertTrue(result is Result.Success)
        assertTrue(fileSystem.exists(pathToFile.toPath()))
    }

    @Test
    fun `When file for backup not exist return error`() = runTest {
        val fileSystem = FakeFileSystem()
        val pathToBackupFile = "backup_file"
        val pathToFile = "original_file"
        val restoreServerFromBackUp = RestoreServerFromBackUp(dispatchers = dispatchers, fileSystem = fileSystem)
        val result = restoreServerFromBackUp.invoke(pathToBackupFile, pathToFile)

        assertTrue(result is Result.Error)
        assertTrue(result.error is TorrserverError.Server.FileNotExist)
    }

    @Test
    fun `When copy backup file throw exception`() = runTest {
        val pathToBackupFile = "backup_file"
        val pathToFile = "original_file"
        val fileSystem: FileSystem = mockk(relaxed = true) {
            every { exists(any()) } returns true
            every { copy(pathToBackupFile.toPath(), pathToFile.toPath()) } throws IOException("Permission error")
        }
        val restoreServerFromBackUp = RestoreServerFromBackUp(dispatchers = dispatchers, fileSystem = fileSystem)
        val result = restoreServerFromBackUp.invoke(pathToBackupFile, pathToFile)

        assertTrue(result is Result.Error)
        assertTrue(result.error is TorrserverError.Unknown)
    }
}