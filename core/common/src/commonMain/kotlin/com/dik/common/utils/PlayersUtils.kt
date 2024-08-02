package com.dik.common.utils

import com.dik.common.Platform
import com.dik.common.Players

fun playersForPlatform(platform: Platform = platformName()): List<Players> {
    return Players.values().filter { it.platforms.contains(platform) && it != Players.SYSTEM_DEFULT_PLAYER }
}