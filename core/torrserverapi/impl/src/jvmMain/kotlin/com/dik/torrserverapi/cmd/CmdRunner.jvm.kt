package com.dik.torrserverapi.cmd

import java.io.BufferedReader
import java.io.InputStreamReader

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object KmpCmdRunner : CmdRunner {
    private val runnedProcesses = mutableListOf<Process>()

    override fun runCmdCommand(command: String) {
        val fullCommand = arrayOf("sh", "-c", command)
        val process = Runtime.getRuntime().exec(fullCommand)
        val reader = BufferedReader(InputStreamReader(process.inputStream))
        var line: String? = reader.readLine()
        while (line != null) {
            println(line)
            line = reader.readLine()
        }
        runnedProcesses.add(process)

//        process.waitFor()
    }

    override fun stopRunnedProcesses() {
        runnedProcesses.forEach {it.destroy() }
    }

}