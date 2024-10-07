package com.dik.torrentlist.di

import com.dik.appsettings.api.model.AppSettings
import com.dik.common.AppDispatchers
import com.dik.common.cmd.CmdRunner
import com.dik.common.cmd.KmpCmdRunner
import com.dik.settings.SettingsFeatureApi
import com.dik.themoviedb.MoviesTheMovieDbApi
import com.dik.themoviedb.SearchTheMovieDbApi
import com.dik.themoviedb.TvEpisodesTheMovieDbApi
import com.dik.themoviedb.TvSeasonsTheMovieDbApi
import com.dik.torrentlist.screens.main.AddMagnetLink
import com.dik.torrentlist.screens.main.AddTorrentFile
import com.dik.torrentlist.screens.main.FindThumbnailForTorrent
import com.dik.torrserverapi.di.TorrserverApi
import com.dik.torrserverapi.server.MagnetApi
import com.dik.torrserverapi.server.TorrentApi
import com.dik.torrserverapi.server.TorrserverCommands
import com.dik.torrserverapi.server.TorrserverStuffApi
import org.koin.core.Koin
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.koinApplication
import org.koin.dsl.module

object KoinModules {

    val koin: Koin by lazy {
        koinApplication {

        }.koin
    }

    fun init(dependencies: TorrentListDependencies): Koin {
        koin.loadModules(listOf(module {
            single<TorrserverApi> { dependencies.torrServerApi() }
            single<TorrserverStuffApi> { dependencies.torrServerApi().torrserverStuffApi() }
            single<TorrentApi> { dependencies.torrServerApi().torrentApi() }
            single<MagnetApi> { dependencies.torrServerApi().magnetApi() }
            single<TorrserverCommands> { dependencies.torrServerApi().torrserverCommands() }
            single<AppDispatchers> { dependencies.dispatchers() }
            factory<CmdRunner> { KmpCmdRunner }
            factory<SettingsFeatureApi> { dependencies.settingsFeatureApi() }
            single<AppSettings> { dependencies.appSettings() }
            single<SearchTheMovieDbApi> { dependencies.theMovieDbApi().searchApi() }
            single<MoviesTheMovieDbApi> { dependencies.theMovieDbApi().movieApi() }
            single<TvEpisodesTheMovieDbApi> { dependencies.theMovieDbApi().tvEpisodesApi() }
            single<TvSeasonsTheMovieDbApi> { dependencies.theMovieDbApi().tvSeasonsApi() }
            factoryOf(::AddTorrentFile).bind<AddTorrentFile>()
            factoryOf(::FindThumbnailForTorrent).bind<FindThumbnailForTorrent>()
            factoryOf(::AddMagnetLink).bind<AddMagnetLink>()
        }))

        return koin
    }
}

internal inline fun <reified T> inject(): T {
    return KoinModules.koin.get()
}