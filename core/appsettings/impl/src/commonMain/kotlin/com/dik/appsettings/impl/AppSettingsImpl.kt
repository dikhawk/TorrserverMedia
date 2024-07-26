package com.dik.appsettings.impl

import com.dik.appsettings.api.model.AppSettings
import com.dik.common.Players
import com.dik.common.toPlayer
import com.russhwolf.settings.Settings
import com.russhwolf.settings.get

class AppSettingsImpl(private val settings: Settings) : AppSettings {

    override var defaultPlayer: Players
        get() = settings.get("default_player", Players.SYSTEM_DEFULT_PLAYER.shortName).toPlayer()
        set(value) {
            settings.putString("default_player", value.shortName)
        }
}