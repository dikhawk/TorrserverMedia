package com.dik.appsettings.api.di

import com.dik.appsettings.api.model.AppSettings
import com.dik.moduleinjector.BaseApi

interface AppSettingsApi: BaseApi {
    fun appSettings(): AppSettings
}