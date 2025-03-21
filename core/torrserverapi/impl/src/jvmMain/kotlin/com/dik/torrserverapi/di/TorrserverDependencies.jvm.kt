package com.dik.torrserverapi.di

import com.dik.common.AppDispatchers
import com.dik.moduleinjector.BaseDependencies

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual interface TorrserverDependencies : BaseDependencies {
    actual fun dispatchers(): AppDispatchers
}