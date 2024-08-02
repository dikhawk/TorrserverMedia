package com.dik.torrserverapi.cmd

import com.dik.common.Platform
import com.dik.common.utils.platformName
import com.dik.common.utils.readOutput
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object KmpServerCommands : ServerCommands {
    override fun startServer(pathToServerFile: String) {
/*        val makeExecutableCommand = "chmod +x $pathToServerFile"
        val startServerCommand = "cd $pathToServer && ./$serverName"
        val fullCommand = arrayOf("sh", "-c", "$makeExecutableCommand && $startServerCommand")
        val process = Runtime.getRuntime().exec(fullCommand)

        val reader = BufferedReader(InputStreamReader(process.inputStream))
        var line: String? = reader.readLine()
        while (line != null) {
            println(line)
            line = reader.readLine()
        }*/

        when (platformName()) {
            Platform.LINUX -> startServerOnLinux(pathToServerFile)
            else -> { TODO("Not yet implemented") }
        }
    }

    private fun startServerOnLinux(pathToServerFile: String) {
        if (pathToServerFile.isEmpty()) return

        val serverFile = File(pathToServerFile)
        if (!serverFile.exists()) throw RuntimeException("Server file not found")

        val makeExecutableCommand = "chmod +x $pathToServerFile"
        val startServerCommand = "cd ${serverFile.parent} && ./${serverFile.name}"

        val c = listOf("sh", "-c", "$makeExecutableCommand && $startServerCommand").joinToString(" ")
        val startServerProcess = ProcessBuilder(
            listOf("sh", "-c", "$makeExecutableCommand && $startServerCommand")
        ).start()
        println(startServerProcess.readOutput())
    }
}