package com.dik.torrserverapi.utils

import okio.FileSystem
import okio.Path.Companion.toPath

fun String.fileToByteArray(): ByteArray {
    return FileSystem.SYSTEM.read(this.toPath()) { readByteString().toByteArray() }
}

expect fun defaultDirectory(): String