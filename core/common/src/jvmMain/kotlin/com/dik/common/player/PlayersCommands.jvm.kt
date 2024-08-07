package com.dik.common.player

import com.dik.common.Platform
import com.dik.common.utils.platformName

actual fun platformPlayersCommands(): PlayersCommands {
    return when(platformName()) {
        Platform.LINUX -> LinuxPlayersCommands()

        else -> throw UnsupportedOperationException("Unsupported platform")
    }
}