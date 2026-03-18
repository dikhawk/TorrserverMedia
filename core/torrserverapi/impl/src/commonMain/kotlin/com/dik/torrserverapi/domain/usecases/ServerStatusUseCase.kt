package com.dik.torrserverapi.domain.usecases

import com.dik.common.onError
import com.dik.common.onSuccess
import com.dik.torrserverapi.domain.SystemProcessProvider
import com.dik.torrserverapi.domain.filemanager.FileManager
import com.dik.torrserverapi.server.ServerConfig
import com.dik.torrserverapi.server.TorrserverStatus
import com.dik.torrserverapi.server.api.TorrserverApiClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class ServerStatusUseCase(
    private val fileManager: FileManager,
    private val config: ServerConfig,
    private val systemProcessProvider: SystemProcessProvider,
    private val torrserverApiClient: TorrserverApiClient,
) {

    operator fun invoke(): Flow<TorrserverStatus> = flow {
        var isInstalled = fileManager.exists(config.pathToServerFile)

        //Repeat if server not installed
        while (!isInstalled) {
            isInstalled = fileManager.exists(config.pathToServerFile)
            emit(TorrserverStatus.General.NotInstalled)
            delay(300)
        }

        var isServerStarted = systemProcessProvider.isProcessRunning(config.torrServerFileName)
        var repeatServerStarted = 0
        val maxRepeatServerStarted = 5

        //Repeat if server not started
        while (!isServerStarted) {
            isServerStarted = systemProcessProvider.isProcessRunning(config.torrServerFileName)
            if (repeatServerStarted <= maxRepeatServerStarted) {
                emit(TorrserverStatus.General.Running)
                repeatServerStarted++
            } else {
                emit(TorrserverStatus.General.Error("Server not started"))
            }
            delay(300)
        }

        while (true) {
            val result = torrserverApiClient.echo()

            result.onSuccess {
                emit(TorrserverStatus.General.Started)
            }.onError {
                emit(TorrserverStatus.General.Stopped)
            }

            delay(300)
        }
    }
}