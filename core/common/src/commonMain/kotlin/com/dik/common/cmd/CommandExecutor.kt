package com.dik.common.cmd

interface CommandExecutor {

    companion object {
        fun instance(): CommandExecutor {
            return commandExecutorInstance()
        }
    }

    fun run(command: String)

    fun runAndWaitResult(command: String): String

    fun stopRunedProcesses()
}

internal expect fun commandExecutorInstance(): CommandExecutor