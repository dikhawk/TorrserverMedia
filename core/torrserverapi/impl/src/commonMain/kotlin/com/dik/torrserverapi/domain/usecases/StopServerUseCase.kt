package com.dik.torrserverapi.domain.usecases

import com.dik.common.Result
import com.dik.torrserverapi.server.TorrserverStatus
import com.dik.torrserverapi.server.api.TorrserverApiClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class StopServerUseCase(
    private val torrserverApiClient: TorrserverApiClient,
) {

    operator fun invoke(): Flow<TorrserverStatus> = flow {
        emit(TorrserverStatus.General.Stoping)

        torrserverApiClient.stopServer()

        repeat(10) {
            val echo = torrserverApiClient.echo()

            if (echo is Result.Success) {
                emit(TorrserverStatus.General.Stopped)
                return@repeat
            }
            delay(500)
        }

        emit(TorrserverStatus.General.Error("Server not stopped"))
    }
}