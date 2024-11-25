package com.dik.common.player

import android.content.Context
import android.content.Intent
import android.net.Uri
import java.net.URLConnection

actual fun platformPlayersCommands(deps: PlatformPlayersDependencies): PlayersCommands {
    return AndroidPlayersCommands(deps)
}

class AndroidPlayersCommands(private val deps: PlatformPlayersDependencies) : PlayersCommands {

    override suspend fun playFileInDefaultPlayer(fileName: String, fileUrl: String) {
        deps.context().openInPlayer(fileName, fileUrl)
    }

    override suspend fun playFile(fileName: String, fileUrl: String, player: Player) {
        deps.context().openInPlayer(fileName, fileUrl)
    }

    private fun Context.openInPlayer(fileName: String, fileUrl: String) {
        val mimeType = URLConnection.guessContentTypeFromName(fileName)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(Uri.parse(fileUrl), mimeType)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(intent)
    }
}

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual interface PlatformPlayersDependencies {
    fun context(): Context
}