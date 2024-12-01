package com.dik.torrentlist.di

import android.content.Context
import com.dik.common.player.PlatformPlayersDependencies
import com.dik.common.player.PlayersCommands
import com.dik.common.player.platformPlayersCommands
import com.dik.torrserverapi.model.TorrserverServiceManager
import org.koin.dsl.module

internal actual fun platformModule(dependencies: TorrentListDependencies) = module {
    factory<PlatformPlayersDependencies> {
        object : PlatformPlayersDependencies {
            override fun context(): Context = get()
        }
    }
    factory<PlayersCommands> { platformPlayersCommands(get()) }
    single<TorrserverServiceManager> { dependencies.torrserverServiceManager() }
}