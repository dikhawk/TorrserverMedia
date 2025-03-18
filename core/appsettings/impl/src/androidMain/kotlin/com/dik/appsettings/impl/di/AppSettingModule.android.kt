package com.dik.appsettings.impl.di

import android.content.Context
import com.dik.common.AppDispatchers
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import org.koin.dsl.module

internal actual fun appSettingModule(dependencies: AppSettingsDependencies) = module {
    single<AppDispatchers> { dependencies.dispatchers() }
    single<Context> { dependencies.context() }
    single<Settings> { SharedPreferencesSettings.Factory(get()).create("app_settings") }
}