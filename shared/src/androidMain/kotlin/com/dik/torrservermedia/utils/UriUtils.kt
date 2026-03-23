package com.dik.torrservermedia.utils

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
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
    val fileName = this.getFileName(context)
    val cacheFile = File(context.cacheDir.path,fileName)
    BufferedInputStream(context.contentResolver.openInputStream(this)).use { inputStream ->
        BufferedOutputStream(cacheFile.outputStream()).use { outputStream ->
            inputStream.copyTo(outputStream)
        }
    }

    return cacheFile.path
}

fun Uri.getFileName(context: Context): String {
    var result: String? = null

    if (this.scheme == "content") {
        val cursor = context.contentResolver.query(this, null, null, null, null)
        cursor.use { cursor ->
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index != -1) {
                    result = cursor.getString(index)
                }
            }
        }
    }

    if (result == null) {
        result = this.path
        val cut = result?.lastIndexOf('/') ?: -1
        if (cut != -1) {
            result = result?.substring(cut + 1)
        }
    }

    return Uri.decode(result) ?: "unknown_file_${System.currentTimeMillis()}"
}