package com.dik.common

import com.dik.common.utils.platformName
import com.dik.common.utils.playersForPlatform
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception

actual fun String.playContent(player: Players) {
    val playCommand = player.gePlayCommand().map { if (it == PATH_TO_FILE) this else it }

    val processBuilder = ProcessBuilder(playCommand)
    processBuilder.start()
}

private fun Players.gePlayCommand(): List<String> {
    if (this == Players.SYSTEM_DEFULT_PLAYER) return defaultPlayCommand()

    val platform = platformName()
    val command = this.commands.find { it.platform == platform }

    if (platform == Platform.LINUX && command != null) {
        val text = playerCommandForLinux(command) ?: defaultPlayCommand()
        return playerCommandForLinux(command) ?: defaultPlayCommand()
    } else {
        return command?.playFile?: defaultPlayCommand()
    }
}

private fun playerCommandForLinux(command: Command): List<String>? {
    val playFileCommand = command.playFile.toMutableList()
    var playerNameCmd: String = playFileCommand.first()
    playerNameCmd = whichLinux(playerNameCmd) ?: flatpackAplicationId(playerNameCmd) ?: ""

    if (playerNameCmd.isNotEmpty()) {
        playFileCommand[0] = playerNameCmd

        return playFileCommand
    }

    return null
}

private fun defaultPlayCommand(): List<String>{
    val platform = platformName()
    val defaultPlayerCommand = Players.SYSTEM_DEFULT_PLAYER.commands
        .find { it.platform == platform }

    if (platform == Platform.LINUX) {
        return defaultPlayCommandForLinux()
    }

    return defaultPlayerCommand?.playFile ?: emptyList()
}

private fun defaultPlayCommandForLinux(): List<String> {
    playersForPlatform().forEach {
        val command = it.commands.find { it.platform == Platform.LINUX }
        if (command != null) {
            val playCommand = playerCommandForLinux(command)
            if (playCommand != null) return playCommand
        }

    }

    return emptyList()
}

private fun whichLinux(playerName: String): String? {
    val process = ProcessBuilder(listOf("which", playerName)).start()
    val output = process.readOutput()
    process.waitFor()

    return if (output.isNullOrEmpty()) null else playerName
}

private fun flatpackAplicationId(playerName: String): String? {
    val output = runCommand("flatpak", "list")

    if (output == null) return null

    return output.lineSequence()
        .map { it.split("\t") }
        .firstOrNull { it.size > 1 && it[0].contains(playerName, ignoreCase = true) }
        ?.get(1)
        ?.trim()
}

private fun runCommand(vararg command: String): String? {
    var output: String? = null

    try {
        val process = ProcessBuilder(*command).start()
        output = process.readOutput()
        process.waitFor()
    } catch (e: Exception){
        println(e.toString())
    }

    return output
}

private fun Process.readOutput(): String {
    val output = StringBuilder()

    BufferedReader(InputStreamReader(this.inputStream)).use { reader ->
        reader.forEachLine { line -> output.appendLine(line) }
        reader
    }

    return output.toString()
}