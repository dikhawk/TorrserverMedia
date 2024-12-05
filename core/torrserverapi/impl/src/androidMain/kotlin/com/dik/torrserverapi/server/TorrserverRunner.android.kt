package com.dik.torrserverapi.server

import com.dik.common.Result
import com.dik.common.cmd.KmpCmdRunner
import com.dik.torrserverapi.TorrserverError
import java.io.File

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal actual class TorrserverRunnerImpl(
    private val config: ServerConfig,
) : TorrserverRunner {
    override suspend fun run(): Result<Unit, TorrserverError> {
        try {
            val torrserver = File(config.pathToServerFile)
            val makeExecutableCommand = "chmod +x ${torrserver.absolutePath}"
            val startServerCommand = "cd ${torrserver.parent} && ./${torrserver.name} -k"

            KmpCmdRunner.run("$makeExecutableCommand && $startServerCommand")
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Error(TorrserverError.Unknown(e.toString()))
        }
    }
}