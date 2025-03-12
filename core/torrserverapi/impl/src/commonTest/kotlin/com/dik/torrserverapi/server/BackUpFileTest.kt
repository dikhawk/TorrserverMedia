package com.dik.torrserverapi.server

import com.dik.common.Result
import com.dik.torrserverapi.TorrserverError
import io.mockk.every
import io.mockk.mockk
import okio.FileSystem
import okio.IOException
import okio.Path.Companion.toPath
import okio.fakefilesystem.FakeFileSystem
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BackUpFileTest {

    @Test
    fun `Should create a backup when file exists`() {
        val fileSystem = FakeFileSystem()
        val backupFile = BackupFile(fileSystem)

        val file = "test.txt".toPath()
        val backup = "backup.txt".toPath()

        fileSystem.write(file) { writeUtf8("Hello") }

        val result = backupFile("test.txt", "backup.txt")

        assertTrue(result is Result.Success)
        assertTrue(fileSystem.exists(backup))
        assertEquals("Hello", fileSystem.read(backup) { readUtf8() })
    }

    @Test
    fun `Should return error when file does not exist`() {
        val fileSystem = FakeFileSystem()
        val backupFile = BackupFile(fileSystem)

        val result = backupFile("missing.txt", "backup.txt")

        assertTrue(result is Result.Error)
        assertTrue(result.error is TorrserverError.Server.FileNotExist)
    }

    @Test
    fun `Should return error when delete fails`() {
        val fileSystem: FileSystem = mockk(relaxed = true) {
            every { exists(any()) } returns true
            every { delete(any()) } throws IOException("Delete failed")
        }
        val backupFile = BackupFile(fileSystem)

        val file = "test.txt".toPath()
        fileSystem.write(file) { writeUtf8("Hello") }

        val result = backupFile("test.txt", "backup.txt")

        assertTrue(result is Result.Error)
        assertEquals("java.io.IOException: Delete failed", (result.error as TorrserverError.Unknown).message)
    }

    @Test
    fun `Should return error when copy fails`() {
        val fileSystem: FileSystem = mockk(relaxed = true) {
            every { exists(any()) } returns true
            every { delete(any()) } returns Unit
            every { copy(any(), any()) } throws IOException("Copy failed")
        }
        val backupFile = BackupFile(fileSystem)

        val file = "test.txt".toPath()
        fileSystem.write(file) { writeUtf8("Hello") }

        val result = backupFile("test.txt", "backup.txt")

        assertTrue(result is Result.Error)
        assertEquals("java.io.IOException: Copy failed", (result.error as TorrserverError.Unknown).message)
    }
}