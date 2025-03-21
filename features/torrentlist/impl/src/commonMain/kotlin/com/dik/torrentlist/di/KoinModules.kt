package com.dik.torrentlist.di

import com.dik.appsettings.api.model.AppSettings
import com.dik.common.AppDispatchers
import com.dik.common.cmd.CmdRunner
import com.dik.common.cmd.KmpCmdRunner
import com.dik.common.i18n.LocalizationResource
import com.dik.common.platform.PlatformEvents
import com.dik.common.platform.WindowAdaptiveClient
import com.dik.settings.SettingsFeatureApi
import com.dik.themoviedb.MoviesTheMovieDbApi
import com.dik.themoviedb.SearchTheMovieDbApi
import com.dik.themoviedb.TvEpisodesTheMovieDbApi
import com.dik.themoviedb.TvSeasonsTheMovieDbApi
import com.dik.torrentlist.screens.main.AddMagnetLink
import com.dik.torrentlist.screens.main.AddTorrentFile
import com.dik.torrentlist.screens.main.FindPosterForTorrent
import com.dik.torrserverapi.di.TorrserverApi
import com.dik.torrserverapi.server.MagnetApi
import com.dik.torrserverapi.server.TorrentApi
import com.dik.torrserverapi.server.TorrserverCommands
import com.dik.torrserverapi.server.TorrserverStuffApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.koinApplication
import org.koin.dsl.module

object KoinModules {

    private val mutex = Mutex()

    @Volatile
    var koin: Koin? = null
        private set

    fun init(dependencies: TorrentListDependencies) {
        if (koin != null) return

        runBlocking {
            mutex.withLock {
                if (koin == null) {
                    koin = koinApplication {
                        koinConfiguration(dependencies).invoke(this)
                        torrentListModules(dependencies)
                    }.koin
                }
            }
        }
    }

    private fun KoinApplication.torrentListModules(dependencies: TorrentListDependencies) {
        modules(
            torrentListModule(dependencies),
            useCasesModule(),
            platformModule(dependencies)
        )
    }
}

internal fun useCasesModule() = module {
    factoryOf(::AddTorrentFile).bind<AddTorrentFile>()
    factoryOf(::FindPosterForTorrent).bind<FindPosterForTorrent>()
    factoryOf(::AddMagnetLink).bind<AddMagnetLink>()
}

internal fun torrentListModule(dependencies: TorrentListDependencies) = module {
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
    single<PlatformEvents> { dependencies.platformEvents() }
    single<WindowAdaptiveClient> { dependencies.windowAdaptive() }
    single<LocalizationResource> { dependencies.localizationResource() }
}

internal expect fun koinConfiguration(dependencies: TorrentListDependencies): KoinAppDeclaration

internal inline fun <reified T> inject(): T {
    return KoinModules.koin!!.get()
}