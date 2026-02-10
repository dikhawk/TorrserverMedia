package com.dik.common.player

import com.dik.common.Platform
import com.dik.common.cmd.CommandExecutor
import java.io.File

class WindowsPlayersCommands: PlayersCommands {

    private val commandExecutor: CommandExecutor = CommandExecutor.instance()

    override suspend fun playFileInDefaultPlayer(fileName: String, fileUrl: String) {
        var command = defaultPlayerCommand(fileName) ?: throw RuntimeException("Not found supported program")

        command = command.replace("%1", fileUrl)
        commandExecutor.run(command)
    }

    private fun defaultPlayerCommand(pathToFile: String): String? {
        val programName = getProgramName(pathToFile) ?: return null
        val command = "ftype $programName"

        val output = commandExecutor.runAndWaitResult(command).trim()

        if (output.isEmpty()) return null

        return output.split("=").last()
    }

    private fun getProgramName(pathToFile: String): String? {
        val file = File(pathToFile)
        val command = "reg query \"HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\FileExts\\.${file.extension}\\UserChoice\" /v Progid"

        val output =  commandExecutor.runAndWaitResult(command).trim()

        if (output.isEmpty()) return null

        return output.split(" ").last()
    }

    override suspend fun playFile(fileName: String, fileUrl: String, player: Player) {
        if (player == Player.DEFAULT_PLAYER) {
            playFileInDefaultPlayer(fileName, fileUrl)
            return
        }

        var playCommand = findPlayer(fileName, player) ?: throw RuntimeException("Player $player not installed")

        playCommand = playCommand.replace("%1", fileUrl)

        commandExecutor.run(playCommand)
    }

    private fun findPlayer(fileName: String, player: Player): String? {
        val extension = File(fileName).extension
        val programName = player.programName.find { it.platform == Platform.WINDOWS } ?: throw RuntimeException("Player $player not found")
        val findCommand = "ftype | findstr /i \"${programName.name}\" | findstr /i \".$extension"

        val result = commandExecutor.runAndWaitResult(findCommand).split("\n")
        if (result.isEmpty()) return null

        val output = commandExecutor.runAndWaitResult(findCommand).split("\n").first()

        return output.split("=").last()
    }
}