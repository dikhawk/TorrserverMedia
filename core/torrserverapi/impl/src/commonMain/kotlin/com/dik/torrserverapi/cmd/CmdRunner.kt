package com.dik.torrserverapi.cmd

interface CmdRunner {
    fun runCmdCommand(command: String)

    fun stopRunnedProcesses()
}

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect object KmpCmdRunner: CmdRunner