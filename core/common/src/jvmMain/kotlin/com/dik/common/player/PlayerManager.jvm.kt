package com.dik.common.player

import com.dik.common.Platform
import com.dik.common.cmd.CommandExecutor
import com.dik.common.utils.platformName

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object PlayerManagerFactory {

    fun instance(): PlayerManager {
        return when (platformName()) {
            Platform.LINUX -> PlayerManagerLinux(CommandExecutor.instance())
            Platform.WINDOWS -> PlayerManagerWindows(CommandExecutor.instance())
            else -> throw IllegalArgumentException("Platform not supported")
        }
    }
}