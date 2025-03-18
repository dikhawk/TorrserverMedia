package com.dik.appsettings.impl.di

import com.dik.common.AppDispatchers
import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.Settings
import org.koin.dsl.module

internal actual fun appSettingModule(dependencies: AppSettingsDependencies) = module {
    single<AppDispatchers> { dependencies.dispatchers() }
    single<Settings> { PreferencesSettings.Factory().create("app_settings") }
}