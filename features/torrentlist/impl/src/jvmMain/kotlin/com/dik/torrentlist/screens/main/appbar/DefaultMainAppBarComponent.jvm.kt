package com.dik.torrentlist.screens.main.appbar

import io.github.vinceglb.filekit.core.PlatformFile
import kotlinx.coroutines.CoroutineDispatcher

internal actual suspend fun PlatformFile.absolutePath(dispatcher: CoroutineDispatcher): String? {
    return path
}