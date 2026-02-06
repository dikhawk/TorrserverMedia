package com.dik.torrserverapi.server

internal interface ServerConfig {
    companion object {
        const val TORRSERVER_DIR = "TorrServerMedia"
        const val FILE_NAME_SERVER = "TorrServer"
        const val BACKUP_FILE_NAME = "old_$FILE_NAME_SERVER"
    }

    val torrserverHost: String
    val pathToServerFile: String
    val pathToBackupServerFile: String
    val torrServerFileName: String
    val backUpTorrServerFileName: String
}