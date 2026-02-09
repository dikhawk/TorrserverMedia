package com.dik.torrserverapi.server

import com.dik.common.AppDispatchers
import com.dik.common.Result
import com.dik.common.cmd.CommandExecutor
import com.dik.torrserverapi.TorrserverError
import io.ktor.utils.io.CancellationException
import kotlinx.coroutines.withContext
import java.io.File

internal class TorrserverRunnerAndroid(
    private val config: ServerConfig,
    private val dispatcher: AppDispatchers,
    private val commandExecutor: CommandExecutor,
) : TorrserverRunner {

    override suspend fun run(): Result<Unit, TorrserverError> =
        withContext(dispatcher.ioDispatcher()) {
            try {
                if (config.pathToServerFile.isEmpty())
                    return@withContext Result.Error(
                        TorrserverError.Server
                            .WrongConfiguration("Path to file is empty ${config.pathToServerFile}")
                    )

                val serverFile = File(config.pathToServerFile)
                if (!serverFile.exists()) {
                    return@withContext Result.Error(
                        TorrserverError.Server
                            .FileNotExist("Server file not found: ${config.pathToServerFile}")
                    )
                }

                val torrserver = File(config.pathToServerFile)
                val makeExecutableCommand = "chmod +x '${torrserver.absolutePath}'"
                val startServerCommand = "cd '${torrserver.parent}' && ./${torrserver.name} -k"

                commandExecutor.run("$makeExecutableCommand && $startServerCommand")
                return@withContext Result.Success(Unit)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                return@withContext Result.Error(TorrserverError.Unknown(e.message ?: ""))
            }
        }
}