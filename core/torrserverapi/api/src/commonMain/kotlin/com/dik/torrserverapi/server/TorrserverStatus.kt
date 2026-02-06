package com.dik.torrserverapi.server

sealed interface TorrserverStatus {

    sealed interface Install : TorrserverStatus {
        data object Start : Install
        data object Installing : Install
        data class Progress(val progress: Double) : Install
        data object Installed : Install
        data class PlatformNotSupported(val msg: String) : Install
        data class Error(val msg: String) : Install
    }

    sealed interface General : TorrserverStatus {
        data object Running : General
        data object Started : General
        data object Stoping : General
        data object Stopped : General
        data object NotInstalled : General
        data class Error(val msg: String): General
    }

    sealed interface CheckLatestVersion : TorrserverStatus {
        data class AvailableNewVersion(val msg: String) : CheckLatestVersion
        data object Checking : CheckLatestVersion
        data object VersionIsActual : CheckLatestVersion
        data class Error(val msg: String) : CheckLatestVersion
    }

    data class Unknown(val msg: String) : TorrserverStatus
}