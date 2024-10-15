package com.dik.torrserverapi.di

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.KoinAppDeclaration

internal actual fun koinConfiguration(dependencies: TorrserverDependencies): KoinAppDeclaration = {
    androidContext(dependencies.context())
}
