package com.dik.appsettings.impl.di

import com.dik.common.AppDispatchers
import com.dik.moduleinjector.BaseDependencies

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect interface AppSettingsDependencies : BaseDependencies {
    fun dispatchers(): AppDispatchers
}