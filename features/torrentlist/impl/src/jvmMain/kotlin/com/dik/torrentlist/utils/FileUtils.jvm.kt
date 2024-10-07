package com.dik.torrentlist.utils

import java.io.File
import java.net.URI
import java.nio.file.Paths

actual fun String.uriToPath(): String {
    return Paths.get(URI(this)).toAbsolutePath().toString()
}

actual fun String.isFileExist(): Boolean {
    return File(this).exists()
}