package com.dik.common.cmd

internal actual fun commandExecutorInstance(): CommandExecutor {
    return CommandExecutorAndroid()
}