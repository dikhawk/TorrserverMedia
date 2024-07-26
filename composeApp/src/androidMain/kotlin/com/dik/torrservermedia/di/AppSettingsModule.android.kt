package com.dik.torrservermedia.di

import android.content.Context
import com.dik.appsettings.impl.di.AppSettingsDependencies
import com.dik.common.AppDispatchers

actual fun appSettingsDependencies() = object : AppSettingsDependencies {
    override fun dispatchers(): AppDispatchers = inject()
    override fun context(): Context = inject()
}