package com.dik.torrserverapi.data

import com.dik.torrserverapi.domain.usecases.CheckNewVersionUseCase
import com.dik.torrserverapi.domain.usecases.InstallTorrserverUseCase
import com.dik.torrserverapi.domain.usecases.RestartServerUseCase
import com.dik.torrserverapi.domain.usecases.ServerStatusUseCase
import com.dik.torrserverapi.domain.usecases.StartServerUseCase
import com.dik.torrserverapi.domain.usecases.StopServerUseCase
import com.dik.torrserverapi.server.TorrserverManager

internal class TorrserverManagerImpl(
    private val installTorrserverUseCase: InstallTorrserverUseCase,
    private val startServerUseCase: StartServerUseCase,
    private val stopServerUseCase: StopServerUseCase,
    private val checkNewVersionUseCase: CheckNewVersionUseCase,
    private val restartServerUseCase: RestartServerUseCase,
    private val serverStatusUseCase: ServerStatusUseCase
) : TorrserverManager {

    override fun observeTorrserverStatus() = serverStatusUseCase()

    override fun installOrUpdate() = installTorrserverUseCase()

    override fun start() = startServerUseCase()

    override fun stop() = stopServerUseCase()

    override fun restart() = restartServerUseCase()

    override fun checkNewVersion() = checkNewVersionUseCase()
}