package com.dik.torrserverapi.data

import com.dik.torrserverapi.domain.usecases.CheckNewVersionUseCase
import com.dik.torrserverapi.domain.usecases.InstallTorrserverUseCase
import com.dik.torrserverapi.domain.usecases.RestartServerUseCase
import com.dik.torrserverapi.domain.usecases.StartServerUseCase
import com.dik.torrserverapi.domain.usecases.StopServerUseCase
import com.dik.torrserverapi.server.TorrserverManager
import com.dik.torrserverapi.server.TorrserverStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach

internal class TorrserverManagerImpl(
    private val installTorrserverUseCase: InstallTorrserverUseCase,
    private val startServerUseCase: StartServerUseCase,
    private val stopServerUseCase: StopServerUseCase,
    private val checkNewVersionUseCase: CheckNewVersionUseCase,
    private val restartServerUseCase: RestartServerUseCase,
) : TorrserverManager {

    private val serverStatus: MutableStateFlow<TorrserverStatus> =
        MutableStateFlow(TorrserverStatus.Unknown("Default status"))

    override fun observeTorserverStatus(): StateFlow<TorrserverStatus> = serverStatus.asStateFlow()

    override fun installOrUpdate() = installTorrserverUseCase()
        .onEach { serverStatus.emit(it) }

    override fun start() = startServerUseCase()
        .onEach { serverStatus.emit(it) }

    override fun stop() = stopServerUseCase()
        .onEach { serverStatus.emit(it) }

    override fun restart() = restartServerUseCase()
        .onEach { serverStatus.emit(it) }

    override fun checkNewVersion() = checkNewVersionUseCase()
        .onEach { serverStatus.emit(it) }
}