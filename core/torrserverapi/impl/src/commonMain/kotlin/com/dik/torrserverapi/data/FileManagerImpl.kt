package com.dik.torrserverapi.data

import com.dik.common.AppDispatchers
import com.dik.torrserverapi.domain.filemanager.FileManager
import kotlinx.coroutines.withContext
import okio.FileSystem
import okio.Path.Companion.toPath

internal class FileManagerImpl(
    private val fileSystem: FileSystem,
    private val dispatchers: AppDispatchers
) : FileManager {

    override suspend fun exists(path: String): Boolean =
        withContext(dispatchers.ioDispatcher()) {
            fileSystem.exists(path.toPath())
        }

    override suspend fun copy(source: String, target: String) =
        withContext(dispatchers.ioDispatcher()) {
            fileSystem.copy(source = source.toPath(), target = target.toPath())
        }

    override suspend fun delete(path: String) =
        withContext(dispatchers.ioDispatcher()) {
            fileSystem.delete(path.toPath())
        }

    override suspend fun fileToByteArray(fileToPath: String): ByteArray {
        return withContext(dispatchers.ioDispatcher()) {
            val path = fileToPath.toPath()

            FileSystem.SYSTEM.read(path) { readByteArray() }
        }
    }
}