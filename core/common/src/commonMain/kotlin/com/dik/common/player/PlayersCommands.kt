package com.dik.common.player

interface PlayersCommands {

    suspend fun playFileInDefaultPlayer(fileName: String, fileUrl: String)

    suspend fun playFile(fileName: String, fileUrl: String, player: Player = Player.DEFAULT_PLAYER)
}

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect interface PlatformPlayersDependencies

expect fun platformPlayersCommands(deps: PlatformPlayersDependencies) : PlayersCommands