package com.dik.torrserverapi.server

import com.dik.common.AppDispatchers
import com.dik.common.Result
import com.dik.common.cmd.CommandExecutor
import com.dik.torrserverapi.TorrserverError
import com.dik.torrserverapi.domain.SystemProcessProvider
import io.ktor.utils.io.CancellationException
import kotlinx.coroutines.withContext
import java.io.File

internal class TorrserverRunnerLinux(
    private val config: ServerConfig,
    private val appDispatchers: AppDispatchers,
    private val commandExecutor: CommandExecutor,
    private val systemProcessProvider: SystemProcessProvider
) : TorrserverRunner {

    override suspend fun run(): Result<Unit, TorrserverError> =
        withContext(appDispatchers.ioDispatcher()) {
            try {
                if (config.pathToServerFile.isEmpty())
                    return@withContext Result.Error(
                        TorrserverError.Server
                            .WrongConfiguration("Path to file is empty ${config.pathToServerFile}")
                    )

                val serverFile = File(config.pathToServerFile)
                if (!serverFile.exists())
                    return@withContext Result.Error(
                        TorrserverError.Server.FileNotExist("File not found")
                    )

                val makeExecutableCommand = "chmod +x '${config.pathToServerFile}'"
                val startServerCommand = "cd '${serverFile.parent}' && nohup ./${serverFile.name} -k > /dev/null 2>&1 &"

                commandExecutor.run("$makeExecutableCommand && $startServerCommand")

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