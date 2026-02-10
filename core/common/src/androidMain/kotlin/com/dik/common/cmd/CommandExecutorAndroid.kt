package com.dik.common.cmd

import com.topjohnwu.superuser.Shell

internal class CommandExecutorAndroid : CommandExecutor {

    private val shellJob: Shell.Job by lazy {
        Shell.Builder.create()
            .setFlags(Shell.FLAG_NON_ROOT_SHELL)
            .build()
            .newJob()
    }

    override fun run(command: String) {
        shellJob.add(command).enqueue()
    }

    override fun runAndWaitResult(command: String): String {
        TODO("Not yet implemented")
    }

    override fun stopRunedProcesses() {
        TODO("Not yet implemented")
    }
}