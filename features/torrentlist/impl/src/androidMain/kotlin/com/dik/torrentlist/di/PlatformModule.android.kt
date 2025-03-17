package com.dik.torrentlist.di

import android.content.Context
import com.dik.common.player.PlatformPlayersDependencies
import com.dik.common.player.PlayersCommands
import com.dik.common.player.platformPlayersCommands
import com.dik.torrentlist.screens.main.torrserverbar.TorrServerStarterAndroidPlatform
import com.dik.torrentlist.screens.main.torrserverbar.TorrServerStarterPlatform
import com.dik.torrentlist.utils.FileUtils
import com.dik.torrentlist.utils.FileUtilsAndroid
import com.dik.torrserverapi.model.TorrserverServiceManager
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module


internal actual fun platformModule(dependencies: TorrentListDependencies) = module {
    factory<PlatformPlayersDependencies> {
        object : PlatformPlayersDependencies { override fun context(): Context = get() }
    }
    factory<PlayersCommands> { platformPlayersCommands(get()) }
    single<TorrserverServiceManager> { dependencies.torrserverServiceManager() }
    factoryOf(::TorrServerStarterAndroidPlatform).bind<TorrServerStarterPlatform>()
    factoryOf(::FileUtilsAndroid).bind<FileUtils>()
}