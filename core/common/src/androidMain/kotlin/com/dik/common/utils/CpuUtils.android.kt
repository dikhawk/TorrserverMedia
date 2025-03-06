package com.dik.common.utils

import android.os.Build

actual fun cpuArch(): String {
    val supportedAbis = Build.SUPPORTED_ABIS?.firstOrNull() ?: return "unknown"

    return supportedAbis.split("-").firstOrNull() ?: "unknown"
}