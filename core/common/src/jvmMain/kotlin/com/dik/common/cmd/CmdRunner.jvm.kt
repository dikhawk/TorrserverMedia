package com.dik.common.cmd

import com.dik.common.Platform
import com.dik.common.utils.platformName
import com.dik.common.utils.readOutput

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object KmpCmdRunner : CmdRunner {
    private val runnedProcesses = mutableListOf<Process>()

    override fun run(command: String) {
        val fullCommand = prepareCommand(command)
        val process = ProcessBuilder(fullCommand).start()
        println(process.readOutput())
        runnedProcesses.add(process)
    }

    override fun runAndWaitResult(command: String): String {
        val fullCommand = prepareCommand(command)
        val process = ProcessBuilder(fullCommand).start()

        runnedProcesses.add(process)
        process.waitFor()

        return process.readOutput()
    }

    override fun stopRunnedProcesses() {
        runnedProcesses.forEach { it.destroy() }
    }

    private fun prepareCommand(command: String): List<String> {
        return when(platformName()) {
            Platform.LINUX -> listOf("sh", "-c", command)
            Platform.WINDOWS -> listOf("cmd", "/c", command)
            else -> throw UnsupportedOperationException("Unsupported platform")
        }
    }
}