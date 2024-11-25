package com.dik.common.player

import com.dik.common.Platform
import com.dik.common.utils.platformName

actual fun platformPlayersCommands(deps: PlatformPlayersDependencies): PlayersCommands {
    return when(platformName()) {
        Platform.LINUX -> LinuxPlayersCommands()
        Platform.WINDOWS -> WindowsPlayersCommands()

        else -> throw UnsupportedOperationException("Unsupported platform")
    }
}

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual interface PlatformPlayersDependencies