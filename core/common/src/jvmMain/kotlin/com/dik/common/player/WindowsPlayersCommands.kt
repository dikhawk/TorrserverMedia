package com.dik.common.player

import com.dik.common.Platform
import com.dik.common.cmd.KmpCmdRunner
import kotlinx.coroutines.runBlocking
import java.io.File

fun main () {
    val com = WindowsPlayersCommands()
    val fileName = "Dark.Matter.S01E09.1080p.rus.LostFilm.TV.mkv"
    val fileUrl = "http://127.0.0.1:8090/stream/Dark.Matter.S01E09.1080p.rus.LostFilm.TV.mkv?" +
            "link=9565f12b2c31f15691e7794928943f25db31f81a&index=1&play"
    runBlocking {
        com.playFile(fileName, fileUrl, Player.VLC)
    }
}

class WindowsPlayersCommands: PlayersCommands {

    override suspend fun playFileInDefaultPlayer(fileName: String, fileUrl: String) {
        var command = defaultPlayerCommand(fileName) ?: throw RuntimeException("Not found supported program")

        command = command.replace("%1", fileUrl)
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

    override suspend fun playFile(fileName: String, fileUrl: String, player: Player) {
        var playCommand = findPlayer(fileName, player) ?: throw RuntimeException("Player $player not installed")

        playCommand = playCommand.replace("%1", fileUrl)

        KmpCmdRunner.run(playCommand)
    }

    private fun findPlayer(fileName: String, player: Player): String? {
        val extension = File(fileName).extension
        val programName = player.programName.find { it.platform == Platform.WINDOWS } ?: throw RuntimeException("Player $player not found")
        val findCommand = "ftype | findstr /i \"${programName.name}\" | findstr /i \".$extension"

        val result = KmpCmdRunner.runAndWaitResult(findCommand).split("\n")
        if (result.isEmpty()) return null

        val output = KmpCmdRunner.runAndWaitResult(findCommand).split("\n").first()

        return output.split("=").last()
    }
}