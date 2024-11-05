package com.dik.torrserverapi.server

import android.content.Context
import com.dik.torrserverapi.di.inject

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal actual object TorrserverConfig : ServerConfig {
    override val torrserverHost: String = "http://127.0.0.1:8090"
    override val pathToServerFile: String
        get() {
            val context: Context = inject()
            val serverFileDirectory = context.filesDir.absolutePath + "/TorrServer"

            return "$serverFileDirectory/$torrServerFileName"
        }
    override val pathToBackupServerFile: String
        get() {
            val context: Context = inject()
            val serverFileDirectory = context.filesDir.absolutePath + "/TorrServer"

            return "$serverFileDirectory/$backUpTorrServerFileName"
        }

    override val torrServerFileName: String = "TorrServer"
    override val backUpTorrServerFileName: String = "old_$torrServerFileName"

}