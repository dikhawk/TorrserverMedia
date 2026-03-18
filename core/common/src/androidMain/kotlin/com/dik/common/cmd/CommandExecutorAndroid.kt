package com.dik.common.cmd

import com.topjohnwu.superuser.Shell

internal class CommandExecutorAndroid : CommandExecutor {

    override fun run(command: String) {
        Shell.cmd(command).exec()
    }

    override fun runAndWaitResult(command: String): String {
        val result = Shell.cmd(command).exec()

        return result.out.joinToString(" ")
    }

    override fun stopRunedProcesses() {
        TODO("Not yet implemented")
    }
}