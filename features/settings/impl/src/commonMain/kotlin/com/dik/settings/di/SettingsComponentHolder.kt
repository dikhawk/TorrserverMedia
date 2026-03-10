package com.dik.settings.di

import com.dik.moduleinjector.ComponentHolder
import com.dik.settings.SettingsFeatureApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock


object SettingsComponentHolder :
    ComponentHolder<SettingsFeatureApi, SettingsDependencies> {

    private var componentHolder: SettingsComponent? = null
    private val mutex = Mutex()

    override fun init(dependencies: SettingsDependencies) {
        if (componentHolder == null) {
            runBlocking(Dispatchers.Default) {
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
