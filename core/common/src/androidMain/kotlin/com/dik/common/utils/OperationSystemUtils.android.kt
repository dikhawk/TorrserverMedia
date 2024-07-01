package com.dik.common.utils

import com.dik.common.Platform
import com.dik.common.toPlatform

actual fun platformName(): Platform {
    return "Android".toPlatform()
}