package com.dik.torrentlist.domain

internal sealed interface ServerStatus {

    sealed interface General : ServerStatus {
        data object Running : General
        data object Started : General
        data object Stoping : General
        data object Stopped : General
        data object NotInstalled : General
        data class Error(val msg: String) : General
    }

    data class Unknown(val msg: String) : ServerStatus
}