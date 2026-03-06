package com.dik.torrservermedia.di

import com.dik.appsettings.impl.di.AppSettingsDependencies
import com.dik.common.AppDispatchers

actual fun appSettingsDependencies() = object : AppSettingsDependencies {
    override fun dispatchers(): AppDispatchers = inject()
}