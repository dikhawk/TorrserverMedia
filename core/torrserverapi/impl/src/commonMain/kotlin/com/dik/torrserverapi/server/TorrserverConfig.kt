package com.dik.torrserverapi.server

internal interface ServerConfig {
    val torrserverHost: String
    val pathToServerFile: String
    val pathToBackupServerFile: String
    val torrServerFileName: String
    val backUpTorrServerFileName: String
}

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal expect object TorrserverConfig : ServerConfig