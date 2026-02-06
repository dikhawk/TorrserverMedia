package com.dik.torrserverapi.domain.usecases

import com.dik.torrserverapi.server.TorrserverStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapConcat

internal class RestartServerUseCase(
    private val startServerUseCase: StartServerUseCase,
    private val stopServerUseCase: StopServerUseCase,
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<TorrserverStatus> = stopServerUseCase()
        .flatMapConcat { status ->
            if (status == TorrserverStatus.General.Stopped) {
                startServerUseCase()
            } else {
                emptyFlow()
            }
        }
}