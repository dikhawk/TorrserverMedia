package com.dik.appsettings.impl

import com.dik.appsettings.api.model.AppSettings
import com.dik.common.player.Player
import com.dik.common.player.toPlayer
import com.russhwolf.settings.Settings
import com.russhwolf.settings.get

class AppSettingsImpl(private val settings: Settings) : AppSettings {

    override var defaultPlayer: Player
        get() = settings.get("default_player", Player.DEFAULT_PLAYER.shortName).toPlayer()
        set(value) {
            settings.putString("default_player", value.shortName)
        }
}