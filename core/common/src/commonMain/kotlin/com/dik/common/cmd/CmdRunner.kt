package com.dik.common.cmd

interface CmdRunner {
    fun run(command: String)

    fun runAndWaitResult(command: String): String

    fun stopRunnedProcesses()
}

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect object KmpCmdRunner: CmdRunner