package com.dik.torrserverapi.server

import com.dik.common.AppDispatchers
import com.dik.common.Result
import com.dik.common.cmd.CommandExecutor
import com.dik.torrserverapi.TorrserverError
import com.dik.torrserverapi.domain.SystemProcessProvider
import io.ktor.utils.io.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File

internal class TorrserverRunnerWindows(
    private val config: ServerConfig,
    private val dispatchers: AppDispatchers,
    private val commandExecutor: CommandExecutor,
    private val systemProcessProvider: SystemProcessProvider
) : TorrserverRunner {

    override suspend fun run(): Result<Unit, TorrserverError> =
        withContext(dispatchers.ioDispatcher()) {
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

                val startServerCommand =
                    "cd \"${serverFile.parent}\" && start /B .\\${serverFile.name} -k >null 2>&1"

                commandExecutor.run(startServerCommand)

                delay(500)

                if (systemProcessProvider.isProcessRunning(ServerConfig.FILE_NAME_SERVER)) {
                    return@withContext Result.Success(Unit)
                } else {
                    return@withContext Result.Error(TorrserverError.Server.NotStarted)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                return@withContext Result.Error(TorrserverError.Unknown(e.message ?: ""))
            }
        }
}