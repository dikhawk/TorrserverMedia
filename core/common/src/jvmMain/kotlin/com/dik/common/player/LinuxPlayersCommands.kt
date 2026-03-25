package com.dik.common.player

import com.dik.common.Platform
import com.dik.common.cmd.CommandExecutor


class LinuxPlayersCommands : PlayersCommands {

    private val commandExecutor: CommandExecutor = CommandExecutor.instance()

    override suspend fun playFileInDefaultPlayer(fileName: String, fileUrl: String) {
        val defaultProgramName = getDefaultProgramName(fileName)

        if (defaultProgramName != null) {
            playFile(fileUrl, defaultProgramName)
            return
        }

        playfileInExistPlayer(fileName, fileUrl)
    }

    private suspend fun playfileInExistPlayer(fileName: String, fileUrl: String) {
        val players = getPlayersForLinux()

        players.forEach { player ->
            val programName = getProgramName(player)
            if (programName != null) {
                playFile(fileName = fileName, fileUrl = fileUrl, player = player)
                return
            }
        }

        throw RuntimeException("There is no default program to open the file.")
    }

    override suspend fun playFile(fileName: String, fileUrl: String, player: Player) {
        if (player == Player.DEFAULT_PLAYER) {
            playFileInDefaultPlayer(fileName, fileUrl)
            return
        }

        val programName = getProgramName(player)

        if (programName == null) {
            playFileInDefaultPlayer(fileName, fileUrl)
            return
        }

        playFile(fileUrl, programName)
    }

    private fun playFile(fileUrl: String, programName: String) {
        val command = "$programName \"$fileUrl\""
        commandExecutor.run(command)
    }

    private fun getPlayersForLinux(): List<Player> =
        Player.values().filter { it.platforms.contains(Platform.LINUX) }

    private fun getProgramName(player: Player): String? {
        val programName = player.programName.find { it.platform == Platform.LINUX }

        if (programName == null) return null

        return which(programName.name) ?: flatpackApplicationId(programName.name)
    }

    private fun which(playerName: String): String? {
        val output = commandExecutor.runAndWaitResult("which $playerName")

        return if (output.isEmpty()) null else playerName
    }

    private fun flatpackApplicationId(playerName: String): String? {
        val output = commandExecutor.runAndWaitResult("flatpak list")

        if (output.isEmpty()) return null

        return output.lineSequence()
            .map { it.split("\t") }
            .firstOrNull { it.size > 1 && it[0].contains(playerName, ignoreCase = true) }
            ?.get(1)
            ?.trim()
    }

    private fun getDefaultProgramName(fileName: String): String? {
        val command = "xdg-mime query default \$(mimetype -b \"${fileName.escapeDoubleQuotes()}\")"
        var programName = prepareProgramName(commandExecutor.runAndWaitResult(command))

        if (programName.isEmpty()) return null

        programName = programName
            .replace("org.kde.", "")
            .replace(".desktop", "")

        return programName.trim()
    }

    private fun String.escapeDoubleQuotes(): String {
        return replace("\"", "\\\"")
    }

    private fun prepareProgramName(rowProgramName: String): String {
        if (rowProgramName.isEmpty()) return rowProgramName

        return rowProgramName
            .replace("org.kde.", "")
            .replace(".desktop", "")
            .trim()
    }
}