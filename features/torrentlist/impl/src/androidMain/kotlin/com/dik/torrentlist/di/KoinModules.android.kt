package com.dik.torrentlist.di

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.KoinAppDeclaration

internal actual fun koinConfiguration(dependencies: TorrentListDependencies): KoinAppDeclaration = {
    androidContext(dependencies.context())
}