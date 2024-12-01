package com.dik.torrentlist.utils

import okio.FileSystem
import okio.Path.Companion.toPath

actual fun String.uriToPath(): String {
    return FileSystem.SYSTEM.canonicalize(this.toPath()).toString()
}

actual fun String.isFileExist(): Boolean {
    return FileSystem.SYSTEM.exists(toPath())
}