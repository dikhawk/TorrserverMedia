package com.dik.common.utils

import com.dik.common.Platform
import com.dik.common.toPlatform

actual fun platformName(): Platform {
    return Platform.ANDROID
}

fun Process.readOutput(): String {
    TODO("Not yet implemented")
}