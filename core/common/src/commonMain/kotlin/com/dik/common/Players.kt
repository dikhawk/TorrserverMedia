package com.dik.common

import com.dik.common.Platform.NOT_SUPPORTED_OS

const val PATH_TO_FILE = "%pathToFile%"

enum class Players(
    val shortName: String,
    val title: String,
    val platforms: List<Platform>,
    val commands: List<Command>
) {
    SYSTEM_DEFULT_PLAYER(
        shortName = "default",
        title = "Default Player",
        platforms = listOf(Platform.WINDOWS, Platform.LINUX, Platform.MAC, Platform.ANDROID),
        commands = listOf(
            Command(platform = Platform.LINUX, playFile = listOf("xdg-open", PATH_TO_FILE)),
            Command(
                platform = Platform.WINDOWS,
                playFile = listOf("cmd", "/c", "start", PATH_TO_FILE)
            ),
            Command(platform = Platform.MAC, playFile = listOf("open", PATH_TO_FILE)),
            Command(platform = Platform.ANDROID, packageName = "org.videolan.vlc"),
        )
    ),
    VLC(
        shortName = "vlc",
        title = "VLC Media Player",
        platforms = listOf(Platform.WINDOWS, Platform.LINUX, Platform.MAC, Platform.ANDROID),
        commands = listOf(
            Command(platform = Platform.LINUX, playFile = listOf("vlc", PATH_TO_FILE)),
            Command(
                platform = Platform.WINDOWS,
                playFile = listOf("cmd", "/c", "start", "vlc", PATH_TO_FILE)
            ),
            Command(platform = Platform.MAC, playFile = listOf("open", "-a", "vlc", PATH_TO_FILE)),
            Command(platform = Platform.ANDROID, packageName = "org.videolan.vlc"),
        )
    ),
    KMPLAYER(
        shortName = "kmplayer",
        title = "KMPlayer",
        platforms = listOf(Platform.WINDOWS, Platform.ANDROID),
        commands = listOf(
            Command(
                platform = Platform.WINDOWS,
                playFile = listOf("cmd", "/c", "start", "kmplayer", PATH_TO_FILE)
            ),
            Command(platform = Platform.ANDROID, packageName = "com.kmplayer"),
        )
    ),
    MPC_HC(
        shortName = "mpc-hc64",
        title = "Media Player Classic",
        platforms = listOf(Platform.WINDOWS),
        commands = listOf(
            Command(
                platform = Platform.WINDOWS,
                playFile = listOf("cmd", "/c", "start", "mpc-hc64", PATH_TO_FILE)
            ),
        )
    ),
    SMPlayer(
        shortName = "smplayer",
        title = "SMPlayer",
        platforms = listOf(Platform.WINDOWS, Platform.LINUX, Platform.MAC),
        commands = listOf(
            Command(platform = Platform.LINUX, playFile = listOf("smplayer", PATH_TO_FILE)),
            Command(
                platform = Platform.WINDOWS,
                playFile = listOf("cmd", "/c", "start", "smplayer", PATH_TO_FILE)
            ),
            Command(
                platform = Platform.MAC,
                playFile = listOf("open", "-a", "smplayer", PATH_TO_FILE)
            ),
        )
    ),
    MXPLAYER(
        shortName = "mp-player",
        title = "MX Player",
        platforms = listOf(Platform.ANDROID),
        commands = listOf(
            Command(platform = Platform.ANDROID, packageName = "com.mxtech.videoplayer")
        )
    ),
    MPV(
        shortName = "mpv-player",
        title = "MPV media player",
        platforms = listOf(Platform.WINDOWS, Platform.LINUX, Platform.MAC, Platform.ANDROID),
        commands = listOf(
            Command(
                platform = Platform.LINUX,
                playFile = listOf("mpv", PATH_TO_FILE)
            ),
            Command(
                platform = Platform.WINDOWS,
                playFile = listOf("cmd", "/c", "start", "mpv", PATH_TO_FILE)
            ),
            Command(
                platform = Platform.MAC,
                playFile = listOf("open", "-a", "mpv", PATH_TO_FILE)
            ),
            Command(platform = Platform.ANDROID, packageName = "is.xyz.mpv")
        )
    ),
    UNDEFINED(
        shortName = "undefined",
        title = "Undefined",
        platforms = emptyList(),
        commands = emptyList()
    );
}

fun String.toPlayer(): Players {
    val player = Players.values().find { player ->
        player.shortName == this
    }

    return player ?: Players.UNDEFINED
}

data class Command(
    val platform: Platform = NOT_SUPPORTED_OS,
    val playFile: List<String> = emptyList(),
    val packageName: String? = null
)

expect fun String.playContent(player: Players)