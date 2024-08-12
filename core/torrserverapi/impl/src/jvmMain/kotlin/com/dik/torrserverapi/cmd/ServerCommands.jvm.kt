package com.dik.torrserverapi.cmd

import com.dik.common.Platform
import com.dik.common.cmd.KmpCmdRunner
import com.dik.common.utils.platformName
import com.dik.common.utils.readOutput
import java.io.File

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object KmpServerCommands : ServerCommands {
    override fun startServer(pathToServerFile: String) {
        when (platformName()) {
            Platform.LINUX -> startServerOnLinux(pathToServerFile)
            Platform.WINDOWS -> startServerOnWindows(pathToServerFile)
            else -> {
                TODO("Not yet implemented")
            }
        }
    }

    private fun startServerOnLinux(pathToServerFile: String) {
        if (pathToServerFile.isEmpty()) return

        val serverFile = File(pathToServerFile)
        if (!serverFile.exists()) throw RuntimeException("Server file not found")

        val makeExecutableCommand = "chmod +x $pathToServerFile"
        val startServerCommand = "cd ${serverFile.parent} && ./${serverFile.name}"

        val startServerProcess = ProcessBuilder(
            listOf("sh", "-c", "$makeExecutableCommand && $startServerCommand")
        ).start()
        println(startServerProcess.readOutput())
    }

    private fun startServerOnWindows(pathToServerFile: String) {
        if (pathToServerFile.isEmpty()) return

        KmpCmdRunner.run(pathToServerFile)
    }
}