package com.dik.torrentlist.screens.main.appbar

import android.content.Context
import com.dik.torrentlist.di.inject
import io.github.vinceglb.filekit.core.PlatformFile
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.File

internal actual suspend fun PlatformFile.absolutePath(dispatcher: CoroutineDispatcher): String? {
    val context: Context = inject()
    val directory = context.cacheDir.absolutePath
    val file = File("$directory/${name}")

    withContext(dispatcher) {
        if (file.exists()) file.delete()
        file.createNewFile()
        file.writeBytes(readBytes())
    }

    return file.absolutePath
}