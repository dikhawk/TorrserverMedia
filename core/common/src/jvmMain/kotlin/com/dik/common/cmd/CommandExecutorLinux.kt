package com.dik.common.cmd

import com.dik.common.utils.readOutput

internal class CommandExecutorLinux : CommandExecutor{

    private val runnedProcesses = mutableListOf<Process>()

    override fun run(command: String) {
        val fullCommand = prepareCommand(command)
        val process = ProcessBuilder(fullCommand).start()
        runnedProcesses.add(process)
    }

    override fun runAndWaitResult(command: String): String {
        val fullCommand = prepareCommand(command)
        val process = ProcessBuilder(fullCommand).start()

        runnedProcesses.add(process)
        process.waitFor()

        return process.readOutput()
    }

    override fun stopRunedProcesses() {
        runnedProcesses.forEach { it.destroy() }
    }

    private fun prepareCommand(command: String): List<String> {
        return listOf("sh", "-c", command)
    }
}