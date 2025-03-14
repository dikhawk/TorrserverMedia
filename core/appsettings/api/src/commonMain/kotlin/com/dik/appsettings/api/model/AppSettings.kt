package com.dik.appsettings.api.model

import com.dik.common.i18n.AppLanguage
import com.dik.common.player.Player

interface AppSettings {
    var defaultPlayer: Player
    var language: AppLanguage
}