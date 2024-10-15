package com.dik.common.player

actual fun platformPlayersCommands(): PlayersCommands {
    return AndroidPlayersCommands()
}

class AndroidPlayersCommands : PlayersCommands {

    override suspend fun playFileInDefaultPlayer(fileName: String, fileUrl: String) {
        TODO("Not yet implemented")
    }

    override suspend fun playFile(fileName: String, fileUrl: String, player: Player) {
        TODO("Not yet implemented")
    }
}