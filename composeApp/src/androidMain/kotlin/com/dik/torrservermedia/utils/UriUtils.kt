package com.dik.torrservermedia.utils

import android.content.Context
import android.net.Uri
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File

fun Uri.pathToFile(context: Context): String? {
    return when (scheme) {
        "file" -> path
        "content" -> copyFileToCache(context)
        else -> throw UnsupportedOperationException("Unsupported scheme: $scheme")
    }
}

fun Uri.copyFileToCache(context: Context): String? {
    val file = File(path)
    val cacheFile = File(context.cacheDir.path + "/${file.name}")
    BufferedInputStream(context.contentResolver.openInputStream(this)).use { inputStream ->
        BufferedOutputStream(cacheFile.outputStream()).use { outputStream ->
            inputStream.copyTo(outputStream)
        }
    }

    return cacheFile.path
}