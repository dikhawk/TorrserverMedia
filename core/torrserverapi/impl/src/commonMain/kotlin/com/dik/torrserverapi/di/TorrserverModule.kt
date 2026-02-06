package com.dik.torrserverapi.di

import com.dik.common.AppDispatchers
import com.dik.common.cmd.CmdRunner
import com.dik.common.cmd.KmpCmdRunner
import com.dik.torrserverapi.SettingsConst
import com.dik.torrserverapi.data.MagnetApiImpl
import com.dik.torrserverapi.data.ServerSettingsApiImpl
import com.dik.torrserverapi.data.TorrentApiImpl
import com.dik.torrserverapi.data.TorrserverManagerImpl
import com.dik.torrserverapi.data.FileManagerImpl
import com.dik.torrserverapi.data.filedownloader.FileDownloaderImpl
import com.dik.torrserverapi.data.http.createHttpClient
import com.dik.torrserverapi.domain.filedownloader.FileDownloader
import com.dik.torrserverapi.domain.filemanager.FileManager
import com.dik.torrserverapi.domain.usecases.BackupFileUseCase
import com.dik.torrserverapi.domain.usecases.CheckNewVersionUseCase
import com.dik.torrserverapi.domain.usecases.DownloadFileUseCase
import com.dik.torrserverapi.domain.usecases.InstallTorrserverUseCase
import com.dik.torrserverapi.domain.usecases.RestartServerUseCase
import com.dik.torrserverapi.domain.usecases.RestoreServerFromBackUpUseCase
import com.dik.torrserverapi.domain.usecases.StartServerUseCase
import com.dik.torrserverapi.domain.usecases.StopServerUseCase
import com.dik.torrserverapi.server.ServerConfig
import com.dik.torrserverapi.server.TorrserverConfig
import com.dik.torrserverapi.server.TorrserverManager
import com.dik.torrserverapi.server.TorrserverApiClientImpl
import com.dik.torrserverapi.server.api.MagnetApi
import com.dik.torrserverapi.server.api.ServerSettingsApi
import com.dik.torrserverapi.server.api.TorrentApi
import com.dik.torrserverapi.server.api.TorrserverApiClient
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.json.Json
import okio.FileSystem
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val torrserverModule = module {
    factory<CmdRunner> { KmpCmdRunner }
    factory<ServerConfig> { TorrserverConfig }
    factory<FileSystem> { FileSystem.SYSTEM }
    singleOf(::MagnetApiImpl).bind<MagnetApi>()
    singleOf(::TorrentApiImpl).bind<TorrentApi>()
    singleOf(::TorrserverApiClientImpl).bind<TorrserverApiClient>()
//    singleOf(::TorrserverCommandsImpl).bind<TorrserverCommands>()
    singleOf(::ServerSettingsApiImpl).bind<ServerSettingsApi>()
    singleOf(::TorrserverManagerImpl).bind<TorrserverManager>()
    singleOf(::FileDownloaderImpl).bind<FileDownloader>()
    singleOf(::FileManagerImpl).bind<FileManager>()
}

internal val useCasesModule = module {
    factoryOf(::BackupFileUseCase).bind<BackupFileUseCase>()
    factoryOf(::CheckNewVersionUseCase).bind<CheckNewVersionUseCase>()
    factoryOf(::DownloadFileUseCase).bind<DownloadFileUseCase>()
    factoryOf(::InstallTorrserverUseCase).bind<InstallTorrserverUseCase>()
    factoryOf(::RestartServerUseCase).bind<RestartServerUseCase>()
    factoryOf(::RestoreServerFromBackUpUseCase).bind<RestoreServerFromBackUpUseCase>()
    factoryOf(::StartServerUseCase).bind<StartServerUseCase>()
    factoryOf(::StopServerUseCase).bind<StopServerUseCase>()
}

internal val httpModule = module {
    single<HttpClient> {
        createHttpClient().config {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }

            install(HttpTimeout) {
                requestTimeoutMillis = 60000
                connectTimeoutMillis = 30000
                socketTimeoutMillis = 30000
            }

            install(HttpCache)

            defaultRequest {
                url(SettingsConst.LOCAL_TORRENT_SERVER)
            }
        }
    }
}

internal fun dependencyModule(deps: TorrserverDependencies) = module {
    single<AppDispatchers> { deps.dispatchers() }
    single<CoroutineScope> {
        CoroutineScope(
            SupervisorJob() + deps.dispatchers().mainDispatcher()
        )
    }
}