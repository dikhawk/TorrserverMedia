package com.dik.torrentlist.di

import androidx.activity.ComponentActivity
import com.dik.common.CurrentActivityProvider
import io.github.vinceglb.filekit.core.FileKit
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.KoinAppDeclaration

internal actual fun koinConfiguration(dependencies: TorrentListDependencies): KoinAppDeclaration = {
    val context = dependencies.context()
    androidContext(context)

    if (context is CurrentActivityProvider) {
        val currentActivity = context.getActiveActivity()
        if (currentActivity is ComponentActivity) {
            FileKit.init(currentActivity)
        }
    }
}