package com.dik.common.player

import com.dik.common.cmd.CommandExecutor
import com.dik.common.player.model.PlatformApp

internal class PlayerManagerLinux(
    private val commandExecutor: CommandExecutor,
) : PlayerManager {

    override suspend fun getAppsList(contentType: String): List<PlatformApp> {
        val cmdResult = commandExecutor.runAndWaitResult(
            """
                gio mime $contentType \
                | sed 's/^[[:space:]]*//' \
                | grep -E '^[^:]+\.desktop$' \
                | sort -u
            """.trimIndent()
        )
        val result = cmdResult.lines()
            .map { it.trim() }
            .filter { it.endsWith(".desktop") && !it.contains(":") }
            .distinct()
            .map {
                PlatformApp(appId = it, appName = getAppName(it))
            }


        return result
    }

    override suspend fun openFile(appId: String, filePath: String) {
        commandExecutor.run(
            """
                gtk-launch $appId $filePath
            """.trimIndent()
        )
    }

    private fun getAppName(appId: String): String {
        val appName = commandExecutor
            .runAndWaitResult(
                """
                    grep '^Name=' /usr/share/applications/$appId | sed 's/^Name=//'
                """.trimIndent()
            )
            .replace("\n", "")

        if (appName.isNotEmpty()) return appName

        val flatPackName = commandExecutor
            .runAndWaitResult(
                """
                grep -m1 '^Name=' /var/lib/flatpak/exports/share/applications/$appId | cut -d= -f2-
            """.trimIndent()
            )
            .replace("\n", "")

        if (flatPackName.isNotEmpty()) return flatPackName

        return appId
    }
}