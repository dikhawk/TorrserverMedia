package com.dik.torrserverapi.server

import com.dik.common.Platform
import com.dik.common.Result
import com.dik.common.ResultProgress
import com.dik.common.utils.platformName
import com.dik.torrserverapi.TorrserverError
import com.dik.torrserverapi.cmd.ServerCommands
import com.dik.torrserverapi.model.TorrserverFile
import com.dik.torrserverapi.utils.defaultDirectory
import kotlinx.coroutines.flow.Flow
import okio.Path

internal class TorrserverCommandsImpl(
    private val serverCommands: ServerCommands,
    private val torrserverStuffApi: TorrserverStuffApi,
    private val installTorrserver: InstallTorrserver
) : TorrserverCommands {

    override suspend fun installServer(): Flow<ResultProgress<TorrserverFile, TorrserverError>> =
        installTorrserver.start(pathToServerFile())

    override suspend fun startServer(): Result<Unit, TorrserverError> {
        try {
            if (torrserverStuffApi.echo() is Result.Success) return Result.Success(Unit)

            serverCommands.startServer(pathToServerFile())

            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Error(TorrserverError.Common.Unknown(e.message ?: ""))
        }
    }

    private fun pathToServerFile() =
        defaultDirectory() + Path.DIRECTORY_SEPARATOR + serverName()

    private fun serverName(): String {
        var serverName = "TorrServer"

        if (platformName() == Platform.WINDOWS) serverName = "$serverName.exe"

        return serverName
    }

    override suspend fun stopServer(): Result<Unit, TorrserverError> {
        return torrserverStuffApi.stopServer()
    }
}