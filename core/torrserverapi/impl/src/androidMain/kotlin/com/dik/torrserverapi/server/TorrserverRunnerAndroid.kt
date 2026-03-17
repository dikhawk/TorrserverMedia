package com.dik.torrserverapi.server

import com.dik.common.AppDispatchers
import com.dik.common.Result
import com.dik.common.cmd.CommandExecutor
import com.dik.torrserverapi.TorrserverError
import com.dik.torrserverapi.domain.SystemProcessProvider
import com.dik.torrserverapi.model.TorrserverServiceManager
import io.ktor.utils.io.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File

internal class TorrserverRunnerAndroid(
    private val config: ServerConfig,
    private val dispatcher: AppDispatchers,
    private val commandExecutor: CommandExecutor,
    private val serviceManager: TorrserverServiceManager,
    private val systemProcessProvider: SystemProcessProvider,
) : TorrserverRunner {

    override suspend fun run(): Result<Unit, TorrserverError> =
        withContext(dispatcher.ioDispatcher()) {
            serviceManager.startService()
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

                val torrServer = File(config.pathToServerFile)
                val makeExecutableCommand = "chmod +x '${torrServer.absolutePath}'"
                val startServerCommand = "cd '${torrServer.parent}' && ./${torrServer.name} -k > /dev/null 2>&1 &"

                commandExecutor.run("$makeExecutableCommand && $startServerCommand")

                delay(500)

                if (systemProcessProvider.isProcessRunning(ServerConfig.FILE_NAME_SERVER)) {
                    return@withContext Result.Success(Unit)
                } else {
                    return@withContext Result.Error(TorrserverError.Server.NotStarted)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                serviceManager.stopService()
                return@withContext Result.Error(TorrserverError.Unknown(e.message ?: ""))
            }
        }
}