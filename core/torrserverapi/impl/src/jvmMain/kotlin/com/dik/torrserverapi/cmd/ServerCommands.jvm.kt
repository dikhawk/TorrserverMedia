package com.dik.torrserverapi.cmd

import java.io.BufferedReader
import java.io.InputStreamReader

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object KmpServerCommands : ServerCommands {
    override fun startServer(serverName: String, pathToServer: String) {
        val startServerCommand = "cd $pathToServer && ./$serverName"
        val fullCommand = arrayOf("sh", "-c", startServerCommand)
        val process = Runtime.getRuntime().exec(fullCommand)

        val reader = BufferedReader(InputStreamReader(process.inputStream))
        var line: String? = reader.readLine()
        while (line != null) {
            println(line)
            line = reader.readLine()
        }
    }
}