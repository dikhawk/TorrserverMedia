package com.dik.common.player

import com.dik.common.Platform
import com.dik.common.Platform.ANDROID
import com.dik.common.Platform.LINUX
import com.dik.common.Platform.MAC
import com.dik.common.Platform.NOT_SUPPORTED_OS
import com.dik.common.Platform.WINDOWS

const val PATH_TO_FILE = "%pathToFile%"

enum class Player(
    val shortName: String,
    val title: String,
    val programName: List<ProgramName>,
    val platforms: List<Platform>,
    val commands: List<Command>
) {
    DEFAULT_PLAYER(
        shortName = "default",
        title = "Default Player",
        platforms = listOf(Platform.WINDOWS, Platform.LINUX, Platform.MAC, Platform.ANDROID),
        programName = listOf(),
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
        programName = listOf(
            ProgramName(platform = LINUX, name = "vlc"),
            ProgramName(platform = MAC, name = "vlc"),
            ProgramName(platform = WINDOWS, name = "vlc"),
            ProgramName(platform = ANDROID, name = "org.videolan.vlc"),
        ),
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
        platforms = listOf(Platform.WINDOWS, Platform.ANDROID, Platform.MAC),
        programName = listOf(
            ProgramName(platform = MAC, name = "kmplayer"),
            ProgramName(platform = WINDOWS, name = "kmplayer"),
            ProgramName(platform = ANDROID, name = "com.kmplayer"),
        ),
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
        programName = listOf(
            ProgramName(platform = WINDOWS, name = "mpc-hc64"),
        ),
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
        programName = listOf(
            ProgramName(platform = LINUX, name = "smplayer"),
            ProgramName(platform = MAC, name = "smplayer"),
            ProgramName(platform = WINDOWS, name = "smplayer"),
        ),
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
        programName = listOf(
            ProgramName(platform = ANDROID, name = "com.mxtech.videoplayer"),
        ),
        commands = listOf(
            Command(platform = Platform.ANDROID, packageName = "com.mxtech.videoplayer")
        )
    ),
    MPV(
        shortName = "mpv-player",
        title = "MPV media player",
        platforms = listOf(Platform.WINDOWS, Platform.LINUX, Platform.MAC, Platform.ANDROID),
        programName = listOf(
            ProgramName(platform = LINUX, name = "mpv"),
            ProgramName(platform = MAC, name = "mpv"),
            ProgramName(platform = WINDOWS, name = "mpv"),
            ProgramName(platform = ANDROID, name = "is.xyz.mpv"),
        ),
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
        programName = listOf(),
        commands = emptyList()
    );
}

fun String.toPlayer(): Player {
    val player = Player.values().find { player ->
        player.shortName == this
    }

    return player ?: Player.UNDEFINED
}

data class Command(
    val platform: Platform = NOT_SUPPORTED_OS,
    val playFile: List<String> = emptyList(),
    val packageName: String? = null
)

data class ProgramName(
    val platform: Platform = NOT_SUPPORTED_OS,
    val name: String
)

expect fun String.playContent(player: Player)