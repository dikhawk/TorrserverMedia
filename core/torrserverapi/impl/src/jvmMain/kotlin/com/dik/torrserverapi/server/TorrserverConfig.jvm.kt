package com.dik.torrserverapi.server

import com.dik.common.Platform
import com.dik.common.utils.platformName
import java.io.File

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal actual object TorrserverConfig : ServerConfig {
    private val configDirectory = getConfigDirectory() + File.separator + "TorrServerMedia"

    override val torrserverHost: String = "http://127.0.0.1:8090"

    override val pathToServerFile: String = when (platformName()) {
        Platform.LINUX -> configDirectory + File.separator + torrServerFileName()
        Platform.WINDOWS -> configDirectory + File.separator + torrServerFileName()
        Platform.MAC -> configDirectory + File.separator + torrServerFileName()
        else -> throw UnsupportedOperationException("Platform not supported ${platformName()}")
    }

    override val pathToBackupServerFile = configDirectory + File.separator + backUpTorrServerFileName()

    override val torrServerFileName: String = torrServerFileName()

    private fun torrServerFileName(): String {
        val fileName = "TorrServer"

        return when (platformName()) {
            Platform.LINUX -> fileName
            Platform.WINDOWS -> "$fileName.exe"
            Platform.MAC -> fileName
            else -> throw UnsupportedOperationException("Platform not supported ${platformName()}")
        }
    }

    override val backUpTorrServerFileName = backUpTorrServerFileName()

    private fun backUpTorrServerFileName() = "old_${torrServerFileName}"

    private fun getConfigDirectory(): String = when (platformName()) {
        Platform.LINUX -> System.getenv("XDG_CONFIG_HOME")
        Platform.WINDOWS -> System.getenv("LOCALAPPDATA")
        Platform.MAC -> "${System.getProperty("user.home")}/Library/Application Support"
        else -> throw UnsupportedOperationException("Platform not supported ${platformName()}")
    }
}