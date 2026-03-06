package com.dik.torrservermedia.di

import com.dik.appsettings.api.model.AppSettings
import com.dik.appsettings.impl.di.AppSettingsComponent
import com.dik.appsettings.impl.di.AppSettingsDependencies
import org.koin.dsl.module

expect fun appSettingsDependencies(): AppSettingsDependencies

val appSettingsModule = module {
    single<AppSettings> {
        AppSettingsComponent.get(appSettingsDependencies()).appSettings()
    }
}