package com.dik.torrserverapi.server

import com.dik.torrserverapi.SettingsConst

internal class ServerConfigLinux : ServerConfig {

    override val torrserverHost: String
        get() = SettingsConst.LOCAL_TORRENT_SERVER

    override val pathToServerFile: String
        get() = "${configDirectory()}/${ServerConfig.FILE_NAME_SERVER}"

    override val pathToBackupServerFile: String
        get() = "${configDirectory()}/${ServerConfig.BACKUP_FILE_NAME}"

    override val torrServerFileName: String
        get() = ServerConfig.FILE_NAME_SERVER

    override val backUpTorrServerFileName: String
        get() = ServerConfig.BACKUP_FILE_NAME

    private fun configDirectory(): String {
        val dir = System.getenv("XDG_CONFIG_HOME")
            ?: "${System.getProperty("user.home")}/.config"

        return "$dir/${ServerConfig.TORRSERVER_DIR}"
    }
}