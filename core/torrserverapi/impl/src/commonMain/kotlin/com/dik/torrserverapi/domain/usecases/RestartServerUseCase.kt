package com.dik.torrserverapi.domain.usecases

import com.dik.torrserverapi.server.TorrserverStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class RestartServerUseCase(

) {

    operator fun invoke(): Flow<TorrserverStatus> = flow {

    }
}