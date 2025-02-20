package com.dik.torrentlist.utils

import android.content.Context
import android.net.Uri
import com.dik.common.AppDispatchers
import kotlinx.coroutines.withContext
import okio.FileSystem
import okio.Path.Companion.toPath
import java.io.File

internal class FileUtilsAndroid(
    private val context: Context,
    private val appDispatchers: AppDispatchers,
): FileUtils {

    override suspend fun uriToPath(uri: String): String = withContext(appDispatchers.ioDispatcher()) {
        FileSystem.SYSTEM.canonicalize(uri.toPath()).toString()
    }

    override suspend fun isFileExist(path: String): Boolean = withContext(appDispatchers.ioDispatcher()) {
        FileSystem.SYSTEM.exists(path.toPath())
    }

    override suspend fun absolutPath(path: String): String = withContext(appDispatchers.ioDispatcher()) {
        val fileName = File(path).name
        val directory = context.cacheDir.absolutePath
        val file = File("$directory/${fileName}")

        withContext(appDispatchers.ioDispatcher()) {
            if (file.exists()) file.delete()

            file.createNewFile()
            file.writeBytes(readBytes(Uri.parse(path)))
        }

        file.absolutePath
    }

    private suspend fun readBytes(uri: Uri): ByteArray = withContext(appDispatchers.ioDispatcher()) {
        context
            .contentResolver
            .openInputStream(uri)
            .use { stream -> stream?.readBytes() }
            ?: throw IllegalStateException("Failed to read file")
    }
}