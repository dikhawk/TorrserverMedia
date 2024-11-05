package com.dik.torrserverapi.server

import com.dik.common.Platform
import com.dik.common.utils.platformName
import java.io.File

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal actual object TorrserverConfig : ServerConfig {

    private val defaultDirectory = System.getProperty("user.dir")

    override val torrserverHost: String = "http://127.0.0.1:8090"

    override val pathToServerFile: String = when (platformName()) {
        Platform.LINUX -> defaultDirectory + File.separator + torrServerFileName()
        Platform.WINDOWS -> defaultDirectory + File.separator + torrServerFileName()
        else -> throw UnsupportedOperationException("Platform not supported ${platformName()}")
    }

    override val pathToBackupServerFile = defaultDirectory + File.separator + backUpTorrServerFileName()

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
}