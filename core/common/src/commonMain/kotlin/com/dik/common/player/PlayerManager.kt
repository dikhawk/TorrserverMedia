package com.dik.common.player

abstract class PlayerManager {

    abstract fun openFile(src: String, player: Player = Player.DEFAULT_PLAYER)

    abstract fun supportedPlayers(): List<Player>
}