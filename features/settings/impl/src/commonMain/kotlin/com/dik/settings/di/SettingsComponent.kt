package com.dik.settings.di

import com.dik.settings.SettingsEntry
import com.dik.settings.SettingsEntryImpl
import com.dik.settings.SettingsFeatureApi

internal abstract class SettingsComponent : SettingsFeatureApi {
    companion object {
        fun initAndGet(dependencies: SettingsDependencies): SettingsComponent {

            return object : SettingsComponent() {
                init {
                    KoinModules.init(dependencies)
                }

                override fun start(): SettingsEntry {
                    return SettingsEntryImpl()
                }
            }
        }
    }
}