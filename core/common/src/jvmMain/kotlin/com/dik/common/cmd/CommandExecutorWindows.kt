package com.dik.common.cmd

import com.dik.common.utils.readOutput

internal class CommandExecutorWindows : CommandExecutor {

    private val runedProcesses = mutableListOf<Process>()

    override fun run(command: String) {
        val fullCommand = prepareCommand(command)
        val process = ProcessBuilder(fullCommand).start()
        runedProcesses.add(process)
    }

    override fun runAndWaitResult(command: String): String {
        val fullCommand = prepareCommand(command)
        val process = ProcessBuilder(fullCommand).start()

        runedProcesses.add(process)
        process.waitFor()

        return process.readOutput()
    }

    override fun stopRunedProcesses() {
        runedProcesses.forEach { it.destroy() }
    }

    private fun prepareCommand(command: String): List<String> {
        return listOf("cmd", "/c", "\"$command\"")
    }
}