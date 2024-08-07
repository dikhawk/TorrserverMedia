package com.dik.common.cmd

import com.dik.common.utils.readOutput

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object KmpCmdRunner : CmdRunner {
    private val runnedProcesses = mutableListOf<Process>()

    override fun run(command: String) {
        val fullCommand = listOf("sh", "-c", command)
        val process = ProcessBuilder(fullCommand).start()
        println(process.readOutput())
        runnedProcesses.add(process)
    }

    override fun runAndWaitResult(command: String): String {
        val fullCommand = listOf("sh", "-c", command)
        val process = ProcessBuilder(fullCommand).start()

        runnedProcesses.add(process)
        process.waitFor()

        return process.readOutput()
    }

    override fun stopRunnedProcesses() {
        runnedProcesses.forEach { it.destroy() }
    }
}