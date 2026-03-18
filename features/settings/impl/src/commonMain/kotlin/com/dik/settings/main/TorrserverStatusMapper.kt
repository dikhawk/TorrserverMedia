package com.dik.settings.main

import com.dik.common.converter.toReadableSize
import com.dik.torrserverapi.server.TorrserverStatus

internal fun TorrserverStatus.toServerVersionState(): ServerVersionState {
    return when (this) {
        is TorrserverStatus.CheckLatestVersion.AvailableNewVersion ->
            ServerVersionState.AvailableNewVersion(this.msg)
        TorrserverStatus.CheckLatestVersion.Checking -> ServerVersionState.CheckingNewVersion
        TorrserverStatus.CheckLatestVersion.VersionIsActual -> ServerVersionState.VersionIsActual
        is TorrserverStatus.CheckLatestVersion.Error -> ServerVersionState.Error(this.msg)

        is TorrserverStatus.Install.Error -> ServerVersionState.Error(this.msg)

        is TorrserverStatus.Install.Progress -> ServerVersionState.ProgressUpdating(
            progress = this.progress.toFloat() / 100f,
            currentBytes = this.currentBytes.toReadableSize(),
            totalBytes = this.totalBytes.toReadableSize()
        )

        TorrserverStatus.Install.Installing -> ServerVersionState.PreparingUpdate

        is TorrserverStatus.Install.Installed -> ServerVersionState.UpdateSuccess

        else -> ServerVersionState.Error(this.toString())
    }
}