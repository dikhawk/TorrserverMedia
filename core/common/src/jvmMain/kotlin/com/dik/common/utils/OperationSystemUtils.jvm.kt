package com.dik.common.utils

import com.dik.common.Platform
import com.dik.common.toPlatform
import java.io.BufferedReader
import java.io.InputStreamReader

actual fun platformName(): Platform {
    return System.getProperty("os.name").toPlatform()
}

fun Process.readOutput(): String {
    val output = StringBuilder()

    BufferedReader(InputStreamReader(this.inputStream)).use { reader ->
        reader.forEachLine { line -> output.appendLine(line) }
        reader
    }

    return output.toString()
}