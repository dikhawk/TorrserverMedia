package com.dik.settings.di

import com.dik.appsettings.api.model.AppSettings
import com.dik.common.AppDispatchers
import com.dik.moduleinjector.BaseDependencies
import com.dik.torrserverapi.di.TorrserverApi

interface SettingsDependecies : BaseDependencies {

    fun torrServerApi(): TorrserverApi
    fun dispatchers(): AppDispatchers
    fun appSettings(): AppSettings
}