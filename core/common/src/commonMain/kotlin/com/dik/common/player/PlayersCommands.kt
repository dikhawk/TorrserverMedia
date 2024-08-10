package com.dik.common.player

interface PlayersCommands {

    suspend fun playFileInDefaultPlayer(fileName: String, fileUrl: String)

    suspend fun playFile(fileName: String, fileUrl: String, player: Player = Player.DEFAULT_PLAYER)
}

expect fun platformPlayersCommands() : PlayersCommands