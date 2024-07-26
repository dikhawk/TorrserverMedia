package com.dik.common

import com.dik.common.Platform.ANDROID
import com.dik.common.Platform.LINUX
import com.dik.common.Platform.MAC
import com.dik.common.Platform.WINDOWS

enum class Players(val shortName: String, val title: String, val platforms: List<Platform>) {
    SYSTEM_DEFULT_PLAYER(
        shortName = "default",
        title = "Default Player",
        platforms = listOf(WINDOWS, LINUX, MAC, ANDROID)
    ),
    VLC(
        shortName = "vlc",
        title = "VLC Media Player",
        platforms = listOf(WINDOWS, LINUX, MAC, ANDROID)
    ),
    KMPLAYER(shortName = "kmp", title = "KMPlayer", platforms = listOf(WINDOWS, ANDROID)),
    MPC_HC(shortName = "mpc_hc", title = "Media Player Classic", platforms = listOf(WINDOWS)),
    GOM_PLAYER(
        shortName = "gom-player",
        title = "GOM Player",
        platforms = listOf(WINDOWS, ANDROID)
    ),
    SMPlayer(shortName = "smplayer", title = "SMPlayer", platforms = listOf(WINDOWS, LINUX, MAC)),
    WMP(shortName = "wmp", title = "Windows Media Player", platforms = listOf(WINDOWS)),
    MXPLAYER(shortName = "mp-player", title = "MX Player", platforms = listOf(ANDROID)),
    MPV(
        shortName = "mpv-player",
        title = "MPV media player",
        platforms = listOf(WINDOWS, LINUX, MAC, ANDROID)
    ),
    UNDEFINED(shortName = "undefined", title = "Undefined", platforms = listOf())
}

fun String.toPlayer(): Players {
    val player = Players.values().find { player ->
        player.shortName == this
    }

    return player ?: Players.UNDEFINED
}