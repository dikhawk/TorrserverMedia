package com.dik.settings.di

import com.dik.appsettings.api.model.AppSettings
import com.dik.moduleinjector.ComponentHolder
import com.dik.settings.SettingsFeatureApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock


object SettingsComponentHolder :
    ComponentHolder<SettingsFeatureApi, SettingsDependecies> {

    private var componentHolder: SettingsComponent? = null
    private val mutex = Mutex()

    override fun init(dependencies: SettingsDependecies) {
        if (componentHolder == null) {
            runBlocking {
                mutex.withLock {
                    if (componentHolder == null) {
                        componentHolder = SettingsComponent.initAndGet(dependencies)
                    }
                }
            }
        }
    }

    override fun get(): SettingsFeatureApi {
        checkNotNull(componentHolder) { "Component SettingsComponent not initialized" }
        return componentHolder!!
    }

    override fun reset() {
        componentHolder = null
    }

}
