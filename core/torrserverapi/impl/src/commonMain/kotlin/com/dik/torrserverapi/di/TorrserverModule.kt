package com.dik.torrserverapi.di

import com.dik.common.AppDispatchers
import com.dik.common.cmd.CmdRunner
import com.dik.common.cmd.KmpCmdRunner
import com.dik.torrserverapi.SettingsConst
import com.dik.torrserverapi.data.MagnetApiImpl
import com.dik.torrserverapi.data.ServerSettingsApiImpl
import com.dik.torrserverapi.data.TorrentApiImpl
import com.dik.torrserverapi.data.http.createHttpClient
import com.dik.torrserverapi.domain.BackupFileUseCase
import com.dik.torrserverapi.domain.DownloadFileUseCase
import com.dik.torrserverapi.domain.InstallTorrserverUseCase
import com.dik.torrserverapi.domain.RestoreServerFromBackUpUseCase
import com.dik.torrserverapi.server.ServerConfig
import com.dik.torrserverapi.server.TorrserverCommands
import com.dik.torrserverapi.server.TorrserverCommandsImpl
import com.dik.torrserverapi.server.TorrserverConfig
import com.dik.torrserverapi.server.TorrserverStuffApiImpl
import com.dik.torrserverapi.server.api.MagnetApi
import com.dik.torrserverapi.server.api.ServerSettingsApi
import com.dik.torrserverapi.server.api.TorrentApi
import com.dik.torrserverapi.server.api.TorrserverStuffApi
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
    singleOf(::TorrserverStuffApiImpl).bind<TorrserverStuffApi>()
    singleOf(::TorrserverCommandsImpl).bind<TorrserverCommands>()
    singleOf(::ServerSettingsApiImpl).bind<ServerSettingsApi>()
    factoryOf(::DownloadFileUseCase).bind<DownloadFileUseCase>()
    factoryOf(::InstallTorrserverUseCase).bind<InstallTorrserverUseCase>()
    factoryOf(::BackupFileUseCase).bind<BackupFileUseCase>()
    factoryOf(::RestoreServerFromBackUpUseCase).bind<RestoreServerFromBackUpUseCase>()
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