package com.dik.common.utils

import android.os.Build

actual fun cpuArch(): String {
    return Build.SUPPORTED_ABIS.firstOrNull() ?: "unknown"
}