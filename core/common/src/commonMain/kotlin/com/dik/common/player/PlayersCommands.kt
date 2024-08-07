package com.dik.common.player

interface PlayersCommands {

    suspend fun playFileInDefaultPlayer(pathToFile: String)

    suspend fun playFile(pathToFile: String, player: Player = Player.DEFAULT_PLAYER)
}

expect fun platformPlayersCommands() : PlayersCommands