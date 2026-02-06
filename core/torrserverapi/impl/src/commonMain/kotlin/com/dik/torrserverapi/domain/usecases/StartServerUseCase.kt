package com.dik.torrserverapi.domain.usecases

import co.touchlab.kermit.Logger
import com.dik.common.AppDispatchers
import com.dik.common.Result
import com.dik.torrserverapi.TorrserverError
import com.dik.torrserverapi.server.TorrserverRunner
import com.dik.torrserverapi.server.TorrserverStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

internal class StartServerUseCase(
    private val appDispatchers: AppDispatchers,
    private val torserverRunner: TorrserverRunner,
) {

    operator fun invoke(): Flow<TorrserverStatus> = flow {
        emit(TorrserverStatus.General.Running)
        Logger.i("Server is running")

        val result: Result<Unit, TorrserverError> = torserverRunner.run()

        when(result) {
            is Result.Error -> {
                emit(result.error.toStatusServer())
                Logger.e("Server have error: ${result.error}")
            }
            is Result.Success -> {
                emit(TorrserverStatus.General.Started)
                Logger.i("Server is started")
            }
        }

        return@flow
    }.flowOn(appDispatchers.defaultDispatcher())

    private fun TorrserverError.toStatusServer(): TorrserverStatus {
        return when(this) {
            is TorrserverError.Server.FileNotExist ->
                TorrserverStatus.General.NotInstalled
            is TorrserverError.Server.WrongConfiguration ->
                TorrserverStatus.General.Error(this.message)
            else -> {
                TorrserverStatus.General.Stopped
            }
        }
    }
}