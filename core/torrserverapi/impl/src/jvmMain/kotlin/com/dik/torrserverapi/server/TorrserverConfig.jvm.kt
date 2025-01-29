package com.dik.torrserverapi.server

import com.dik.common.Platform
import com.dik.common.utils.platformName
import java.io.File
import java.nio.file.Paths

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal actual object TorrserverConfig : ServerConfig {

    private val configDirectory = getConfigDirectory() + File.separator + "TorrServerMedia"

    override val torrserverHost: String = "http://127.0.0.1:8090"

    override val pathToServerFile: String = when (platformName()) {
        Platform.LINUX -> configDirectory + File.separator + torrServerFileName()
        Platform.WINDOWS -> configDirectory + File.separator + torrServerFileName()
        else -> throw UnsupportedOperationException("Platform not supported ${platformName()}")
    }

    override val pathToBackupServerFile = configDirectory + File.separator + backUpTorrServerFileName()

    override val torrServerFileName: String = torrServerFileName()

    private fun torrServerFileName(): String {
        val fileName = "TorrServer"

        return when (platformName()) {
            Platform.LINUX -> fileName
            Platform.WINDOWS -> "$fileName.exe"
            else -> throw UnsupportedOperationException("Platform not supported ${platformName()}")
        }
    }

    override val backUpTorrServerFileName = backUpTorrServerFileName()

    private fun backUpTorrServerFileName() = "old_${torrServerFileName}"

    private fun getConfigDirectory(): String = when (platformName()) {
        Platform.LINUX -> {
            val xdgConfigHome = System.getenv("XDG_CONFIG_HOME")
            Paths.get(xdgConfigHome ?: "${System.getProperty("user.home")}/.config").toString()
        }
        Platform.WINDOWS -> {
            val localAppData = System.getenv("LOCALAPPDATA")
            Paths.get(localAppData).resolve("MyApp").toString()
        }
        else -> throw UnsupportedOperationException("Platform not supported ${platformName()}")
    }
}