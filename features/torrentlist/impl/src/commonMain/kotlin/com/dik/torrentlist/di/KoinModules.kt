package com.dik.torrentlist.di

import com.dik.appsettings.api.model.AppSettings
import com.dik.common.AppDispatchers
import com.dik.common.cmd.CommandExecutor
import com.dik.common.i18n.LocalizationResource
import com.dik.common.platform.PlatformEvents
import com.dik.common.platform.WindowAdaptiveClient
import com.dik.settings.SettingsFeatureApi
import com.dik.themoviedb.MoviesTheMovieDbApi
import com.dik.themoviedb.SearchTheMovieDbApi
import com.dik.themoviedb.TvEpisodesTheMovieDbApi
import com.dik.themoviedb.TvSeasonsTheMovieDbApi
import com.dik.torrentlist.screens.main.domain.AddMagnetLinkUseCase
import com.dik.torrentlist.screens.main.domain.AddTorrentFileUseCase
import com.dik.torrentlist.screens.main.domain.FindPosterUseCase
import com.dik.torrserverapi.di.TorrserverApi
import com.dik.torrserverapi.server.TorrserverManager
import com.dik.torrserverapi.server.api.TorrentApi
import com.dik.torrserverapi.server.api.TorrserverApiClient
import kotlinx.coroutines.Dispatchers
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

    var koin: Koin? = null
        private set

    fun init(dependencies: TorrentListDependencies) {
        if (koin != null) return

        runBlocking(Dispatchers.Default) {
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
    factoryOf(::AddTorrentFileUseCase).bind<AddTorrentFileUseCase>()
    factoryOf(::FindPosterUseCase).bind<FindPosterUseCase>()
    factoryOf(::AddMagnetLinkUseCase).bind<AddMagnetLinkUseCase>()
}

internal fun torrentListModule(dependencies: TorrentListDependencies) = module {
    single<TorrserverApi> { dependencies.torrServerApi() }
    single<TorrserverApiClient> { dependencies.torrServerApi().torrserverApiClient() }
    single<TorrentApi> { dependencies.torrServerApi().torrentApi() }
    single<TorrserverManager> { dependencies.torrServerApi().torrserverManager() }
    single<AppDispatchers> { dependencies.dispatchers() }
    factory<CommandExecutor> { CommandExecutor.instance() }
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