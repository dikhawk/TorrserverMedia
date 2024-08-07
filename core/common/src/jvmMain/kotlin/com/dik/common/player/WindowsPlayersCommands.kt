package com.dik.common.player

import com.dik.common.Platform
import com.dik.common.cmd.KmpCmdRunner
import kotlinx.coroutines.runBlocking
import java.io.File

fun main () {
    val com = WindowsPlayersCommands()
    val video = "C:\\Users\\Dik\\Downloads\\Those.About.to.Die.S01E10.1080p.rus.LostFilm.TV.mkv"
    runBlocking {
        com.playFile(video, Player.VLC)
    }
}

class WindowsPlayersCommands: PlayersCommands {

    override suspend fun playFileInDefaultPlayer(pathToFile: String) {
        var command = defaultPlayerCommand(pathToFile) ?: throw RuntimeException("Not found supported program")

        command = command.replace("%1", pathToFile)
        KmpCmdRunner.run(command)
    }

    private fun defaultPlayerCommand(pathToFile: String): String? {
        val programName = getProgramName(pathToFile) ?: return null
        val command = "ftype $programName"

        val output = KmpCmdRunner.runAndWaitResult(command).trim()

        if (output.isEmpty()) return null

        return output.split("=").last()
    }

    private fun getProgramName(pathToFile: String): String? {
        val file = File(pathToFile)
        val command = "reg query \"HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\FileExts\\.${file.extension}\\UserChoice\" /v Progid"

        val output =  KmpCmdRunner.runAndWaitResult(command).trim()

        if (output.isEmpty()) return null

        return output.split(" ").last()
    }

    override suspend fun playFile(pathToFile: String, player: Player) {
        var playCommand = findPlayer(pathToFile, player) ?: throw RuntimeException("Player $player not installed")

        playCommand = playCommand.replace("%1", pathToFile)

        KmpCmdRunner.run(playCommand)
    }

    private fun findPlayer(pathToFile: String, player: Player): String? {
        val extension = File(pathToFile).extension
        val programName = player.programName.find { it.platform == Platform.WINDOWS } ?: throw RuntimeException("Player $player not found")
        val findCommand = "ftype | findstr /i \"${programName.name}\" | findstr /i \".$extension"

        val result = KmpCmdRunner.runAndWaitResult(findCommand).split("\n")
        if (result.isEmpty()) return null

        val output = KmpCmdRunner.runAndWaitResult(findCommand).split("\n").first()

        return output.split("=").last()
    }
}