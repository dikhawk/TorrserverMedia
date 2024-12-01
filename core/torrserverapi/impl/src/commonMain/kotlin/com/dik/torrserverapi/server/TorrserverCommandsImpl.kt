package com.dik.torrserverapi.server

import co.touchlab.kermit.Logger
import com.dik.common.AppDispatchers
import com.dik.common.Result
import com.dik.common.ResultProgress
import com.dik.common.utils.successResult
import com.dik.torrserverapi.TorrserverError
import com.dik.torrserverapi.model.TorrserverFile
import com.dik.torrserverapi.model.TorrserverStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okio.FileNotFoundException
import okio.FileSystem
import okio.Path.Companion.toPath

internal class TorrserverCommandsImpl(
    private val torserverRunner: TorrserverRunner,
    private val torrserverStuffApi: TorrserverStuffApi,
    private val installTorrserver: InstallTorrserver,
    private val restoreServerFromBackUp: RestoreServerFromBackUp,
    private val appDispatchers: AppDispatchers,
    private val scope: CoroutineScope,
    private val config: ServerConfig
) : TorrserverCommands {

    private var monitorServerStatus: Job? = null
    private val torServerStatus =
        MutableSharedFlow<TorrserverStatus>(replay = 1, extraBufferCapacity = 3)

    override suspend fun serverStatus(): SharedFlow<TorrserverStatus> =
        torServerStatus.asSharedFlow()

    override suspend fun installServer(): Flow<ResultProgress<TorrserverFile, TorrserverError>> =
        withContext(appDispatchers.defaultDispatcher()) {
            installTorrserver(config.pathToServerFile, config.pathToBackupServerFile)
        }

    override suspend fun startServer(): Result<Unit, TorrserverError> {
        try {
            torServerStatus.emit(TorrserverStatus.RUNNING)
            var isStartedServer = isServerStarted().successResult() ?: false

            if (isStartedServer) {
                torServerStatus.emit(TorrserverStatus.STARTED)
                startMonitorServerStatus()
                return Result.Success(Unit)
            }

            val isServerInstalledResult = isServerInstalled()
            val isServerInstalled = isServerInstalledResult.successResult() ?: false
            if (!isServerInstalled || (isServerInstalledResult is Result.Error)) {
                torServerStatus.emit(TorrserverStatus.NOT_INSTALLED)
                return Result.Error(TorrserverError.Server.FileNotExist("Server file not exist"))
            }

            torserverRunner.run()

            isStartedServer = isServerStarted().successResult() ?: false
            if (isStartedServer) {
                torServerStatus.emit(TorrserverStatus.STARTED)
                startMonitorServerStatus()
                return Result.Success(Unit)
            }

            torServerStatus.emit(TorrserverStatus.NOT_STARTED)
            return Result.Error(TorrserverError.Server.NotStarted)
        } catch (e: Exception) {
            torServerStatus.emit(TorrserverStatus.NOT_STARTED)
            Logger.e(e.toString())
            return Result.Error(TorrserverError.Unknown(e.message ?: ""))
        }
    }

    override suspend fun stopServer(): Result<Unit, TorrserverError> {
        val result = withContext(appDispatchers.ioDispatcher()) {
            torrserverStuffApi.stopServer()
        }

        stopMonitorServerStatus()

        //A waiting when server stopped
        val tries = 5
        for (t in 1..tries) {
            val echoResult =
                withContext(appDispatchers.ioDispatcher()) { torrserverStuffApi.echo() }
            if (echoResult is Result.Error) break

            delay(1000)
        }

        torServerStatus.emit(TorrserverStatus.STOPPED)
        return result
    }

    override suspend fun isServerInstalled(): Result<Boolean, TorrserverError> {
        val isServerInstalled = withContext(appDispatchers.ioDispatcher()) {
            try {
                val path = config.pathToServerFile.toPath()
                val fileSize = FileSystem.SYSTEM.metadata(path).size ?: 0L
                val isFileExist = FileSystem.SYSTEM.exists(path)

                fileSize > 0 && isFileExist
            } catch (e: FileNotFoundException) {
                Logger.e(e.toString())
                false
            }
        }

        return Result.Success(isServerInstalled)
    }

    override suspend fun isServerStarted(): Result<Boolean, TorrserverError> {
        try {
            val tries = 10
            for (t in 1..tries) {
                val echoResult =
                    withContext(appDispatchers.ioDispatcher()) { torrserverStuffApi.echo() }
                if (echoResult is Result.Success) {
                    return Result.Success(true)
                }

                delay(500)
            }

            return Result.Success(false)
        } catch (e: Exception) {
            Logger.e(e.toString())
            return Result.Error(TorrserverError.Unknown(e.message ?: ""))
        }
    }

    override suspend fun isAvailableNewVersion(): Result<Boolean, TorrserverError> {
        try {
            val serverStatusResult = withContext(appDispatchers.ioDispatcher()) {
                torrserverStuffApi.echo()
            }

            if (serverStatusResult is Result.Error) return Result.Error(serverStatusResult.error)

            val latestServerVersionResult = torrserverStuffApi.checkLatestRelease()

            if (latestServerVersionResult is Result.Error) return Result.Error(
                latestServerVersionResult.error
            )

            if (serverStatusResult is Result.Success && latestServerVersionResult is Result.Success) {
                val localServerVersion = serverStatusResult.data
                val latestServerVersion = latestServerVersionResult.data.tagName
                val isAvailableNewVersion = localServerVersion != latestServerVersion

                return Result.Success(isAvailableNewVersion)
            }

            return Result.Error(TorrserverError.Unknown("Can't check updates"))
        } catch (e: Exception) {
            Logger.e(e.toString())
            return Result.Error(TorrserverError.Unknown(e.message ?: ""))
        }
    }

    override suspend fun restoreServerFromBackUp(): Result<Unit, TorrserverError> {
        return try {
            restoreServerFromBackUp(config.pathToBackupServerFile, config.pathToServerFile)
        } catch (e: Exception) {
            Logger.e(e.toString())
            Result.Error(TorrserverError.Unknown(e.toString()))
        }
    }

    private fun startMonitorServerStatus() {
        monitorServerStatus?.cancel()
        monitorServerStatus = scope.launch(appDispatchers.defaultDispatcher()) {
            while (true) {
                val isServerInstalled = isServerInstalled().successResult() ?: false
                val isServerStarted = isServerStarted().successResult() ?: false

                if (!isServerInstalled && !isServerStarted) {
                    torServerStatus.emit(TorrserverStatus.NOT_INSTALLED)
                    break
                }

                torServerStatus.emit(if (isServerStarted) TorrserverStatus.STARTED else TorrserverStatus.NOT_STARTED)

                delay(500)
            }
        }
    }

    private fun stopMonitorServerStatus() {
        monitorServerStatus?.cancel()
    }
}