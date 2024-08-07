package com.dik.common.player

import java.io.File

class WindowsPlayersCommands: PlayersCommands {

    override suspend fun playFileInDefaultPlayer(pathToFile: String) {
        TODO("Not yet implemented")
    }

    override suspend fun playFile(pathToFile: String, players: Player) {
        TODO("Not yet implemented")
    }

    private fun defaultPlayer(pathToFile: String): String? {
        val file = File(pathToFile)
        val command = "reg query \"HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\FileExts\\.${file.extension}\\UserChoice\" /v Progid"
        val ftype = "ftype program_name"

        TODO("Not yet implemented return")
    }
}