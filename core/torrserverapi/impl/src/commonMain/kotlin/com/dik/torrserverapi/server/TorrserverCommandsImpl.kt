package com.dik.torrserverapi.server

import co.touchlab.kermit.Logger
import com.dik.common.AppDispatchers
import com.dik.common.Result
import com.dik.common.ResultProgress
import com.dik.common.utils.successResult
import com.dik.torrserverapi.TorrserverError
import com.dik.torrserverapi.domain.InstallTorrserverUseCase
import com.dik.torrserverapi.domain.RestoreServerFromBackUpUseCase
import com.dik.torrserverapi.model.TorrserverFile
import com.dik.torrserverapi.server.api.TorrserverStuffApi
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
    private val installTorrserver: InstallTorrserverUseCase,
    private val restoreServerFromBackUp: RestoreServerFromBackUpUseCase,
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
        return try {
            emitStatus(TorrserverStatus.RUNNING, "Server is running")

            if (isServerRunning()) {
                onServerStarted()
                Result.Success(Unit)
            } else {
                ensureServerInstalled()?.let { return it }

                torserverRunner.run()

                if (isServerRunning()) {
                    onServerStarted()
                    Result.Success(Unit)
                } else {
                    emitStatus(
                        TorrserverStatus.NOT_STARTED,
                        "Server not started",
                        isError = true
                    )
                    Result.Error(TorrserverError.Server.NotStarted)
                }
            }
        } catch (e: Exception) {
            emitStatus(TorrserverStatus.NOT_STARTED, e.toString(), isError = true)
            Result.Error(TorrserverError.Unknown(e.message ?: ""))
        }
    }

    override suspend fun stopServer(): Result<Unit, TorrserverError> {
        val result = withIoContext { torrserverStuffApi.stopServer() }

        stopMonitorServerStatus()

        waitUntilServerStops()

        emitStatus(TorrserverStatus.STOPPED, "Server is stopped")
        return result
    }

    override suspend fun isServerInstalled(): Result<Boolean, TorrserverError> {
        val isInstalled = withIoContext {
            val path = config.pathToServerFile.toPath()
            try {
                if (!FileSystem.SYSTEM.exists(path)) return@withIoContext false

                val fileSize = FileSystem.SYSTEM.metadata(path).size ?: 0L
                fileSize > 0
            } catch (e: FileNotFoundException) {
                Logger.e(e.toString())
                false
            }
        }

        return Result.Success(isInstalled)
    }

    override suspend fun isServerStarted(): Result<Boolean, TorrserverError> {
        try {
            repeat(SERVER_START_RETRY_COUNT) {
                val echoResult = withIoContext { torrserverStuffApi.echo() }
                if (echoResult is Result.Success) {
                    return Result.Success(true)
                }

                delay(SERVER_START_RETRY_DELAY_MS)
            }

            return Result.Success(false)
        } catch (e: Exception) {
            Logger.e(e.toString())
            return Result.Error(TorrserverError.Unknown(e.message ?: ""))
        }
    }

    override suspend fun isAvailableNewVersion(): Result<Boolean, TorrserverError> {
        try {
            val serverStatusResult = withIoContext { torrserverStuffApi.echo() }

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
                val installed = isServerInstalled().successResult() ?: false
                val started = isServerStarted().successResult() ?: false

                if (!installed && !started) {
                    emitStatus(TorrserverStatus.NOT_INSTALLED, isError = true)
                    break
                }

                emitStatus(if (started) TorrserverStatus.STARTED else TorrserverStatus.NOT_STARTED)

                delay(STATUS_POLL_DELAY_MS)
            }
        }
    }

    private fun stopMonitorServerStatus() {
        monitorServerStatus?.cancel()
    }

    private suspend fun waitUntilServerStops() {
        repeat(SERVER_STOP_RETRY_COUNT) {
            val echoResult = withIoContext { torrserverStuffApi.echo() }
            if (echoResult is Result.Error) return

            delay(SERVER_STOP_RETRY_DELAY_MS)
        }
    }

    private suspend fun isServerRunning(): Boolean = isServerStarted().successResult() ?: false

    private suspend fun ensureServerInstalled(): Result<Unit, TorrserverError>? {
        val installResult = isServerInstalled()
        val isInstalled = installResult.successResult() ?: false
        return if (!isInstalled || installResult is Result.Error) {
            emitStatus(
                TorrserverStatus.NOT_INSTALLED,
                SERVER_FILE_NOT_EXIST_MESSAGE,
                isError = true
            )
            Result.Error(TorrserverError.Server.FileNotExist(SERVER_FILE_NOT_EXIST_MESSAGE))
        } else {
            null
        }
    }

    private suspend fun onServerStarted() {
        emitStatus(TorrserverStatus.STARTED, "Server is started")
        startMonitorServerStatus()
    }

    private suspend fun emitStatus(
        status: TorrserverStatus,
        message: String? = null,
        isError: Boolean = false
    ) {
        torServerStatus.emit(status)
        message?.let {
            if (isError) {
                Logger.e(it)
            } else {
                Logger.i(it)
            }
        }
    }

    private suspend fun <T> withIoContext(block: suspend () -> T): T =
        withContext(appDispatchers.ioDispatcher()) { block() }

    private companion object {
        const val SERVER_START_RETRY_COUNT = 10
        const val SERVER_STOP_RETRY_COUNT = 5
        const val SERVER_START_RETRY_DELAY_MS = 500L
        const val SERVER_STOP_RETRY_DELAY_MS = 1000L
        const val STATUS_POLL_DELAY_MS = 500L
        const val SERVER_FILE_NOT_EXIST_MESSAGE = "Server file not exist"
    }
}