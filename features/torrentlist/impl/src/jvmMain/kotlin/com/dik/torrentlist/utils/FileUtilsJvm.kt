package com.dik.torrentlist.utils

import com.dik.common.AppDispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URI
import java.nio.file.Paths

internal class FileUtilsJvm(
    private val appDispatchers: AppDispatchers
) : FileUtils {

    override suspend fun uriToPath(uri: String): String = withContext(appDispatchers.ioDispatcher()) {
        Paths.get(URI(uri)).toAbsolutePath().toString()
    }

    override suspend fun isFileExist(path: String): Boolean = withContext(appDispatchers.ioDispatcher()){
        File(path).exists()
    }

    override suspend fun absolutPath(path: String): String = withContext(appDispatchers.ioDispatcher()){
        path
    }
}