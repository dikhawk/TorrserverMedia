package com.dik.common

enum class Platform(val osname: String) {
    LINUX("Linux"),
    WINDOWS("Windows"),
    ANDROID("Android"),
    MAC("Mac"),
    NOT_SUPPORTED_OS("Not Supported Operation system")
}

internal fun String.toPlatform(): Platform {
    return when {
        this.contains(Platform.LINUX.osname, true) -> Platform.LINUX
        this.contains(Platform.WINDOWS.osname, true) -> Platform.WINDOWS
        this.contains(Platform.ANDROID.osname, true) -> Platform.ANDROID
        this.contains(Platform.MAC.osname, true) -> Platform.MAC
        else -> Platform.NOT_SUPPORTED_OS
    }
}