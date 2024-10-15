package com.dik.torrserverapi.utils

import android.content.Context
import com.dik.torrserverapi.di.inject

actual fun defaultDirectory(): String {
    val context: Context = inject()

    return context.filesDir.absolutePath
}