package com.dik.torrserverapi.server

import com.dik.common.AppDispatchers
import com.dik.common.Platform
import com.dik.common.Result
import com.dik.common.ResultProgress
import com.dik.common.utils.platformName
import com.dik.common.utils.repeatIfError
import com.dik.torrserverapi.TorrserverError
import com.dik.torrserverapi.cmd.ServerCommands
import com.dik.torrserverapi.model.TorrserverFile
import com.dik.torrserverapi.utils.defaultDirectory
import kotlinx.coroutines.async
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
    private val appDispatchers: AppDispatchers
) : TorrserverCommands {

    override suspend fun installServer(): Flow<ResultProgress<TorrserverFile, TorrserverError>> =
        withContext(appDispatchers.defaultDispatcher()) { installTorrserver.start(pathToServerFile()) }

    override suspend fun startServer(): Result<Unit, TorrserverError> {
        try {
            val echoResult = withContext(appDispatchers.ioDispatcher()) {
                torrserverStuffApi.echo()
            }
            if (echoResult is Result.Success) return Result.Success(Unit)

            withContext(appDispatchers.defaultDispatcher()) {
                serverCommands.startServer(pathToServerFile())
            }

            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Error(TorrserverError.Common.Unknown(e.message ?: ""))
        }
    }

    private fun pathToServerFile() = defaultDirectory() + Path.DIRECTORY_SEPARATOR + serverName()

    private fun serverName(): String {
        var serverName = "TorrServer"

        if (platformName() == Platform.WINDOWS) serverName = "$serverName.exe"

        return serverName
    }

    override suspend fun stopServer(): Result<Unit, TorrserverError> {
        val result = withContext(appDispatchers.ioDispatcher()) {
            torrserverStuffApi.stopServer()
        }

        //A waiting when server stopped
        var tries = 3
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
            val echoResult = withContext(appDispatchers.ioDispatcher()) {
                torrserverStuffApi.echo()
            }
            val isStarted = echoResult is Result.Success

            return Result.Success(isStarted)
        } catch (e: Exception) {
            return Result.Error(TorrserverError.Common.Unknown(e.message ?: ""))
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

            return Result.Error(TorrserverError.Common.Unknown("Can't check updates"))
        } catch (e: Exception) {
            return Result.Error(TorrserverError.Common.Unknown(e.message ?: ""))
        }
    }
}