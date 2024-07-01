package com.dik.torrserverapi.server

import com.dik.common.Result
import com.dik.torrserverapi.TorrserverError
import com.dik.torrserverapi.cmd.ServerCommands
import com.dik.torrserverapi.model.TorrserverFile
import kotlinx.coroutines.flow.Flow
import java.io.File

internal class TorrserverCommandsImpl(
    private val serverCommands: ServerCommands,
    private val torrserverStuffApi: TorrserverStuffApi,
    private val installTorrserver: InstallTorrserver
) : TorrserverCommands {

    override suspend fun installServer(): Flow<Result<TorrserverFile, TorrserverError>> =
        installTorrserver.start("/home/dik/TorrServer/TorrServer")

    override suspend fun updateServer(): Flow<Result<TorrserverFile, TorrserverError>> {
        TODO("Not yet implemented")
    }

    override suspend fun startServer(): Result<Unit, TorrserverError> {
        try {
            serverCommands.startServer("TorrServer", "/home/dik/TorrServer")

            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Error(TorrserverError.Common.Unknown(e.message ?: ""))
        }
    }

    override suspend fun stopServer(): Result<Unit, TorrserverError> {
        return torrserverStuffApi.stopServer()
    }
}