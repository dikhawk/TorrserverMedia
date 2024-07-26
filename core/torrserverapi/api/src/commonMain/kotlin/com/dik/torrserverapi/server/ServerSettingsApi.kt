package com.dik.torrserverapi.server

import com.dik.common.Result
import com.dik.torrserverapi.TorrserverError
import com.dik.torrserverapi.model.ServerSettings

interface ServerSettingsApi {

    suspend fun saveSettings(serverSettings: ServerSettings): Result<ServerSettings, TorrserverError>
    suspend fun getSettings(): Result<ServerSettings, TorrserverError>
    suspend fun defaultSettings(): Result<ServerSettings, TorrserverError>
}