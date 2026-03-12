package com.dik.torrentlist.data

import com.dik.torrentlist.domain.ServerStatus
import com.dik.torrserverapi.server.TorrserverStatus

internal fun TorrserverStatus.toServerStatus(): ServerStatus = when (this) {

    TorrserverStatus.General.Running -> ServerStatus.General.Running
    TorrserverStatus.General.Started -> ServerStatus.General.Started
    TorrserverStatus.General.Stoping -> ServerStatus.General.Stoping
    TorrserverStatus.General.Stopped -> ServerStatus.General.Stopped
    TorrserverStatus.General.NotInstalled -> ServerStatus.General.NotInstalled
    is TorrserverStatus.General.Error -> ServerStatus.General.Error(msg)

    is TorrserverStatus.Unknown -> ServerStatus.Unknown(msg)
    else -> {
        ServerStatus.Unknown(this.toString())
    }
}
