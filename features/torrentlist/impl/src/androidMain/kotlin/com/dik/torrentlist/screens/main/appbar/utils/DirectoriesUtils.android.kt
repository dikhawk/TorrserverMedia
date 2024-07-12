package com.dik.torrentlist.screens.main.appbar.utils

import android.os.Environment

internal actual fun defaultFilePickerDirectory(): String {
    return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath
}