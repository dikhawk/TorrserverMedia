package com.dik.torrserverapi.server

import com.dik.common.AppDispatchers
import com.dik.common.Platform
import com.dik.common.Result
import com.dik.common.cmd.KmpCmdRunner
import com.dik.common.utils.platformName
import com.dik.torrserverapi.TorrserverError
import com.dik.torrserverapi.server.TorrserverConfig.pathToServerFile
import kotlinx.coroutines.withContext
import java.io.File

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal actual class TorrserverRunnerImpl(
    private val config: ServerConfig,
    private val appDispatchers: AppDispatchers
) : TorrserverRunner {

    override suspend fun run(): Result<Unit, TorrserverError> {
        try {
            when (platformName()) {
                Platform.LINUX -> startServerOnLinux(config.pathToServerFile)
                Platform.WINDOWS -> startServerOnWindows(pathToServerFile)
                else -> return Result.Error(
                    TorrserverError.Server.PlatformNotSupported(
                        "Platform ${platformName().osname} is not supported"
                    )
                )
            }
        } catch (e: Exception) {
            return Result.Error(TorrserverError.Unknown(e.toString()))
        }

        return Result.Success(Unit)
    }

    private suspend fun startServerOnLinux(pathToServerFile: String) {
        if (pathToServerFile.isEmpty()) return

        withContext(appDispatchers.defaultDispatcher()) {
            val serverFile = File(pathToServerFile)
            if (!serverFile.exists()) throw RuntimeException("Server file not found")

            val makeExecutableCommand = "chmod +x $pathToServerFile"
            val startServerCommand = "cd ${serverFile.parent} && ./${serverFile.name} -k"

            KmpCmdRunner.run("$makeExecutableCommand && $startServerCommand")
        }
    }

    private suspend fun startServerOnWindows(pathToServerFile: String) {
        if (pathToServerFile.isEmpty()) return

        withContext(appDispatchers.defaultDispatcher()) {
            KmpCmdRunner.run(pathToServerFile)
        }
    }
}