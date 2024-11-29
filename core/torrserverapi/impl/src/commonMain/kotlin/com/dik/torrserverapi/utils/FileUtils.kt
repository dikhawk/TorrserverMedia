package com.dik.torrserverapi.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.FileSystem
import okio.Path.Companion.toPath

suspend fun fileToByteArray(
    fileToPath: String,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
): ByteArray {
    return withContext(dispatcher) {
        val path = fileToPath.toPath()

        FileSystem.SYSTEM.read(path) { readByteArray() }
    }
}