package com.dik.torrserverapi

import com.dik.common.cmd.CommandExecutor
import com.dik.torrserverapi.domain.SystemProcessProvider

internal class SystemProcessProviderAndroid(
    private val commandExecutor: CommandExecutor,
): SystemProcessProvider {

    override fun isProcessRunning(processName: String): Boolean {
        val checkProcess = commandExecutor.runAndWaitResult(
            "pgrep -x $processName"
        )

        return checkProcess.isNotEmpty()
    }
}