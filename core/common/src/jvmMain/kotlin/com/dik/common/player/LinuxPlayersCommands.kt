package com.dik.common.player

import com.dik.common.Platform
import com.dik.common.cmd.KmpCmdRunner

class LinuxPlayersCommands : PlayersCommands {

    override suspend fun playFileInDefaultPlayer(pathToFile: String) {
        val defaultProgrammName = getDefaultProgrammName(pathToFile)

        if (defaultProgrammName != null) {
            playFile(pathToFile, defaultProgrammName)
            return
        }

        playfileInExistPlayer(pathToFile)
    }

    private suspend fun playfileInExistPlayer(pathToFile: String) {
        val players = getPlayersForLinux()

        players.forEach { player ->
            val programmName = getProgrammName(player)
            if (programmName != null) {
                playFile(pathToFile, player)
                return
            }
        }

        throw RuntimeException("There is no default program to open the file.")
    }

    override suspend fun playFile(pathToFile: String, player: Player) {
        val programmName = getProgrammName(player)

        if (programmName == null) {
            playFileInDefaultPlayer(pathToFile)
            return
        }

        playFile(pathToFile, programmName)
        val command = "$programmName $pathToFile"

        KmpCmdRunner.run(command)
    }

    private suspend fun playFile(pathToFile: String, programmName: String) {
        val command = "$programmName $pathToFile"
        KmpCmdRunner.run(command)
    }

    private fun getPlayersForLinux(): List<Player> =
        Player.values().filter { it.platforms.contains(Platform.LINUX) }

    private suspend fun getProgrammName(player: Player): String? {
        val programName = player.programName.find { it.platform == Platform.LINUX }

        if (programName == null) return null

        return which(programName.name) ?: flatpackAplicationId(programName.name)
    }

    private suspend fun which(playerName: String): String? {
        val output = KmpCmdRunner.runAndWaitResult("which $playerName")

        return if (output.isNullOrEmpty()) null else playerName
    }

    private suspend fun flatpackAplicationId(playerName: String): String? {
        val output = KmpCmdRunner.runAndWaitResult("flatpak list")

        if (output.isNullOrEmpty()) return null

        return output.lineSequence()
            .map { it.split("\t") }
            .firstOrNull { it.size > 1 && it[0].contains(playerName, ignoreCase = true) }
            ?.get(1)
            ?.trim()
    }

    private suspend fun getDefaultProgrammName(pathToFile: String): String? {
        val command = "xdg-mime query default \$(xdg-mime query filetype $pathToFile)"
        var programmName = prepareProgrammName(KmpCmdRunner.runAndWaitResult(command))

        if (programmName.isEmpty()) return null

        programmName = programmName
            .replace("org.kde.", "")
            .replace(".desktop", "")

        return programmName.trim()
    }

    private fun prepareProgrammName(rowProgrammName: String): String {
        if (rowProgrammName.isEmpty()) return rowProgrammName

        return rowProgrammName
            .replace("org.kde.", "")
            .replace(".desktop", "")
            .trim()
    }
}