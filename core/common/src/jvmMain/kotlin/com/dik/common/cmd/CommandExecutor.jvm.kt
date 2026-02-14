package com.dik.common.cmd

import com.dik.common.Platform
import com.dik.common.utils.platformName

internal actual fun commandExecutorInstance(): CommandExecutor {
    return when(platformName()) {
        Platform.LINUX -> CommandExecutorLinux()
        Platform.WINDOWS -> CommandExecutorWindows()
        else -> throw IllegalArgumentException("Not supported os: ${platformName()}")
    }
}