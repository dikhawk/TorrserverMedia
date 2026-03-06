package com.dik.torrservermedia.di

import com.dik.common.AppDispatchers
import com.dik.torrserverapi.di.TorrserverDependencies

internal actual fun torrserverDependencies() = object : TorrserverDependencies {
    override fun dispatchers(): AppDispatchers = inject()

}