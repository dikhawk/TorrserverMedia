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
        this.contains(Platform.LINUX.osname) -> Platform.LINUX
        this.contains(Platform.WINDOWS.osname) -> Platform.WINDOWS
        this.contains(Platform.ANDROID.osname) -> Platform.ANDROID
        this.contains(Platform.MAC.osname) -> Platform.MAC
        else -> Platform.NOT_SUPPORTED_OS
    }
}