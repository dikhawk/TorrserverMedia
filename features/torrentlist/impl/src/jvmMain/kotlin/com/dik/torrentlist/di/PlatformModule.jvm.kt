package com.dik.torrentlist.di

import com.dik.common.player.PlatformPlayersDependencies
import com.dik.common.player.PlayersCommands
import com.dik.common.player.platformPlayersCommands
import com.dik.torrentlist.screens.main.torrserverbar.TorrServerStarterJvmPlatform
import com.dik.torrentlist.screens.main.torrserverbar.TorrServerStarterPlatform
import com.dik.torrentlist.utils.FileUtils
import com.dik.torrentlist.utils.FileUtilsJvm
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module


internal actual fun platformModule(dependencies: TorrentListDependencies) = module {
    factory<PlatformPlayersDependencies> { object: PlatformPlayersDependencies { } }
    factory<PlayersCommands> { platformPlayersCommands(get()) }
    factoryOf(::TorrServerStarterJvmPlatform).bind<TorrServerStarterPlatform>()
    factoryOf(::FileUtilsJvm).bind<FileUtils>()
}