package com.dik.common.utils

actual fun cpuArch(): String {
    return System.getProperty("os.arch")
}