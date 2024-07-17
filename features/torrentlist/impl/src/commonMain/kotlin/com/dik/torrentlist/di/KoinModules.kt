package com.dik.torrentlist.di

import com.dik.common.AppDispatchers
import com.dik.common.cmd.CmdRunner
import com.dik.common.cmd.KmpCmdRunner
import com.dik.settings.SettingsFeatureApi
import com.dik.torrserverapi.di.TorrserverApi
import com.dik.torrserverapi.server.MagnetApi
import com.dik.torrserverapi.server.TorrentApi
import com.dik.torrserverapi.server.TorrserverCommands
import com.dik.torrserverapi.server.TorrserverStuffApi
import org.koin.core.Koin
import org.koin.dsl.koinApplication
import org.koin.dsl.module

object KoinModules {

    val koin: Koin by lazy {
        koinApplication {

        }.koin
    }

    fun init(dependecies: TorrentListDependecies): Koin {
        koin.loadModules(listOf(module {
            single<TorrserverApi> { dependecies.torrServerApi() }
            single<TorrserverStuffApi> { dependecies.torrServerApi().torrserverStuffApi() }
            single<TorrentApi> { dependecies.torrServerApi().torrentApi() }
            single<MagnetApi> { dependecies.torrServerApi().magnetApi() }
            single<TorrserverCommands> { dependecies.torrServerApi().torrserverCommands() }
            single<AppDispatchers> { dependecies.dispatchers() }
            factory<CmdRunner> { KmpCmdRunner }
            factory<SettingsFeatureApi> { dependecies.settingsFeatureApi() }
        }))

        return koin
    }
}

internal inline fun <reified T> inject(): T {
    return KoinModules.koin.get()
}