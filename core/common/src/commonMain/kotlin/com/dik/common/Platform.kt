package com.dik.common

enum class Platform(val osname: String) {
    LINUX("Linux"),
    WINDOWS("Windows"),
    ANDROID("Android"),
    NOT_SUPPORTED_OS("Not Supported Operation system")
}

internal fun String.toPlatform(): Platform {
    return when (this) {
        "Linux" -> Platform.LINUX
        "Windows" -> Platform.WINDOWS
        "Android" -> Platform.ANDROID
        else -> Platform.NOT_SUPPORTED_OS
    }
}