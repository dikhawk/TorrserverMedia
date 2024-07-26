package com.dik.settings.di

import com.dik.settings.SettingsEntry
import com.dik.settings.SettingsEntryImpl
import com.dik.settings.SettingsFeatureApi

internal abstract class SettingsComponent : SettingsFeatureApi {
    companion object {
        fun initAndGet(dependecies: SettingsDependecies): SettingsComponent {

            return object : SettingsComponent() {
                init {
                    KoinModules.init(dependecies)
                }

                override fun start(): SettingsEntry {
                    return SettingsEntryImpl()
                }
            }
        }
    }
}