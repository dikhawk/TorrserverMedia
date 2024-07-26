package com.dik.appsettings.impl.di

import com.dik.appsettings.api.di.AppSettingsApi
import com.dik.appsettings.api.model.AppSettings
import com.dik.common.AppDispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

abstract class AppSettingsComponent: AppSettingsApi {

    companion object {
        private var component: AppSettingsComponent? = null
        private val mutex = Mutex()

        fun get(dependencise: AppSettingsDependencies): AppSettingsComponent {
            if (component == null) {
                runBlocking {
                    mutex.withLock {
                        if (component == null) {
                            component = object : AppSettingsComponent() {
                                init {
                                    KoinModules.init(dependencise)
                                }
                                override fun appSettings(): AppSettings  = inject()
                            }
                        }
                    }
                }
            }

            return component!!
        }
    }
}