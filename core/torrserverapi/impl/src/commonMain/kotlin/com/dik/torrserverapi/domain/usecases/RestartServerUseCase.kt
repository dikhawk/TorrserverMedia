package com.dik.torrserverapi.domain.usecases

import com.dik.torrserverapi.server.TorrserverStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flattenConcat
import kotlinx.coroutines.flow.flowOf

internal class RestartServerUseCase(
    private val startServerUseCase: StartServerUseCase,
    private val stopServerUseCase: StopServerUseCase,
) {

/*    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<TorrserverStatus> = stopServerUseCase()
        .flatMapConcat { status ->
            if (status == TorrserverStatus.General.Stopped) {
                startServerUseCase()
            } else {
                emptyFlow()
            }
        }*/

    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<TorrserverStatus> =
        flowOf(stopServerUseCase(), startServerUseCase()).flattenConcat()
}