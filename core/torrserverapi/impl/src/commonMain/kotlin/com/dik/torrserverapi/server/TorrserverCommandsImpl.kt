package com.dik.torrserverapi.server

import com.dik.common.AppDispatchers
import com.dik.common.Platform
import com.dik.common.Result
import com.dik.common.ResultProgress
import com.dik.common.utils.platformName
import com.dik.common.utils.successResult
import com.dik.torrserverapi.TorrserverError
import com.dik.torrserverapi.cmd.ServerCommands
import com.dik.torrserverapi.model.TorrserverFile
import com.dik.torrserverapi.utils.defaultDirectory
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import okio.FileNotFoundException
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath

internal class TorrserverCommandsImpl(
    private val serverCommands: ServerCommands,
    private val torrserverStuffApi: TorrserverStuffApi,
    private val installTorrserver: InstallTorrserver,
    private val restoreServerFromBackUp: RestoreServerFromBackUp,
    private val appDispatchers: AppDispatchers
) : TorrserverCommands {

    override suspend fun installServer(): Flow<ResultProgress<TorrserverFile, TorrserverError>> =
        withContext(appDispatchers.defaultDispatcher()) {
            installTorrserver(pathToServerFile(), pathToBackupServerFile())
        }

    override suspend fun startServer(): Result<Unit, TorrserverError> {
        try {
            var isStartedServer = isServerStarted().successResult() ?: false

            if (isStartedServer) return Result.Success(Unit)

            withContext(appDispatchers.defaultDispatcher()) {
                serverCommands.startServer(pathToServerFile())
            }

            isStartedServer = isServerStarted().successResult() ?: false
            if (isStartedServer) return Result.Success(Unit)

            restoreServerFromBackUp(pathToBackupServerFile(), pathToServerFile())

            isStartedServer = isServerStarted().successResult() ?: false
            if (isStartedServer) return Result.Success(Unit)

            return Result.Error(TorrserverError.Server.NotStarted)
        } catch (e: Exception) {
            return Result.Error(TorrserverError.Unknown(e.message ?: ""))
        }
    }

    private fun pathToServerFile() = defaultDirectory() + Path.DIRECTORY_SEPARATOR + serverName()

    private fun pathToBackupServerFile() = defaultDirectory() + Path.DIRECTORY_SEPARATOR + backUpServerName()

    private fun serverName(): String {
        var serverName = "TorrServer"

        if (platformName() == Platform.WINDOWS) serverName = "$serverName.exe"

        return serverName
    }

    private fun backUpServerName() = "old_${serverName()}"

    override suspend fun stopServer(): Result<Unit, TorrserverError> {
        val result = withContext(appDispatchers.ioDispatcher()) {
            torrserverStuffApi.stopServer()
        }

        //A waiting when server stopped
        var tries = 5
        for (t in 1..tries) {
            val echoResult = withContext(appDispatchers.ioDispatcher()) { torrserverStuffApi.echo() }
            if (echoResult is Result.Error) break

            delay(1000)
        }

        return result
    }

    override suspend fun isServerInstalled(): Result<Boolean, TorrserverError> {
        val isServerInstalled = withContext(appDispatchers.ioDispatcher()) {
            try {
                val path = pathToServerFile().toPath()
                val fileSize = FileSystem.SYSTEM.metadata(path).size ?: 0L
                val isfileExist = FileSystem.SYSTEM.exists(path)

                fileSize > 0 && isfileExist
            } catch (e: FileNotFoundException) {
                false
            }
        }

        return Result.Success(isServerInstalled)
    }

    override suspend fun isServerStarted(): Result<Boolean, TorrserverError> {
        try {
            var tries = 10
            for (t in 1..tries) {
                val echoResult = withContext(appDispatchers.ioDispatcher()) { torrserverStuffApi.echo() }
                if (echoResult is Result.Success) return Result.Success(true)

                delay(500)
            }

            return Result.Success(false)
        } catch (e: Exception) {
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

            if (latestServerVersionResult is Result.Error) return Result.Error(latestServerVersionResult.error)

            if (serverStatusResult is Result.Success && latestServerVersionResult is Result.Success) {
                val localServerVersion = serverStatusResult.data
                val latestServerVersion = latestServerVersionResult.data.tagName
                val isAvailableNewVersion = localServerVersion != latestServerVersion

                return Result.Success(isAvailableNewVersion)
            }

            return Result.Error(TorrserverError.Unknown("Can't check updates"))
        } catch (e: Exception) {
            return Result.Error(TorrserverError.Unknown(e.message ?: ""))
        }
    }
}