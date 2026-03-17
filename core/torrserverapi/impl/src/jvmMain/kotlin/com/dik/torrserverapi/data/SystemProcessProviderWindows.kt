package com.dik.torrserverapi.data

import com.dik.common.cmd.CommandExecutor
import com.dik.torrserverapi.domain.SystemProcessProvider

internal class SystemProcessProviderWindows(
    private val commandExecutor: CommandExecutor
): SystemProcessProvider {

    override fun isProcessRunning(processName: String): Boolean {
        val checkCommand = "tasklist /FI \"IMAGENAME eq $processName\" /NH"
        val output = commandExecutor.runAndWaitResult(checkCommand)

        return output.isNotEmpty()
    }
}