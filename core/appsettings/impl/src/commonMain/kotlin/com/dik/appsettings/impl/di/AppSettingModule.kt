package com.dik.appsettings.impl.di

import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.Settings
import org.koin.core.module.Module
import org.koin.dsl.module

expect fun appSettingModule(dependencies: AppSettingsDependencies): Module