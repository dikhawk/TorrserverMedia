package com.dik.settings.di

import com.dik.appsettings.api.model.AppSettings
import com.dik.common.AppDispatchers
import com.dik.common.i18n.LocalizationResource
import com.dik.moduleinjector.BaseDependencies
import com.dik.torrserverapi.di.TorrserverApi

interface SettingsDependencies : BaseDependencies {

    fun torrServerApi(): TorrserverApi
    fun dispatchers(): AppDispatchers
    fun appSettings(): AppSettings
    fun localizationResource(): LocalizationResource
}