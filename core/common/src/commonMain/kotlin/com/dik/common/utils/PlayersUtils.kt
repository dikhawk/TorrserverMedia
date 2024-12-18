package com.dik.common.utils

import com.dik.common.Platform
import com.dik.common.player.Player

fun playersForPlatform(platform: Platform = platformName()): List<Player> {
    return Player.entries.filter { it.platforms.contains(platform) && it != Player.DEFAULT_PLAYER }
}