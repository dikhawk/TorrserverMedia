package com.dik.appsettings.impl

import com.dik.appsettings.api.model.AppSettings
import com.dik.common.i18n.AppLanguage
import com.dik.common.i18n.toAppLanguage
import com.dik.common.player.Player
import com.dik.common.player.toPlayer
import com.russhwolf.settings.Settings
import com.russhwolf.settings.get

class AppSettingsImpl(private val settings: Settings) : AppSettings {

    private val defaultPlayerKey = "default_player"
    private val languageKey = "language"

    override var defaultPlayer: Player
        get() = settings[defaultPlayerKey, Player.DEFAULT_PLAYER.shortName].toPlayer()
        set(value) {
            settings.putString(defaultPlayerKey, value.shortName)
        }

    override var language: AppLanguage
        get() = settings[languageKey, AppLanguage.ENGLISH.iso].toAppLanguage()
        set(value) { settings.putString(languageKey, value.iso) }
}