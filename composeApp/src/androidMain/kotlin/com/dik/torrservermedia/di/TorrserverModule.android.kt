package com.dik.torrservermedia.di

import android.content.Context
import com.dik.common.AppDispatchers
import com.dik.torrserverapi.di.TorrserverDependencies

internal actual fun torrserverDependencies() = object : TorrserverDependencies {
    override fun dispatchers(): AppDispatchers = inject()
    override fun context(): Context = inject()
}

