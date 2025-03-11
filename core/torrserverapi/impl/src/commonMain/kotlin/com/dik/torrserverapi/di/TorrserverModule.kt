package com.dik.torrserverapi.di

import com.dik.common.AppDispatchers
import com.dik.common.cmd.CmdRunner
import com.dik.common.cmd.KmpCmdRunner
import com.dik.torrserverapi.http.createHttpClient
import com.dik.torrserverapi.server.BackupFile
import com.dik.torrserverapi.server.DownloadFile
import com.dik.torrserverapi.server.InstallTorrserver
import com.dik.torrserverapi.server.MagnetApi
import com.dik.torrserverapi.server.MagnetApiImpl
import com.dik.torrserverapi.server.RestoreServerFromBackUp
import com.dik.torrserverapi.server.ServerConfig
import com.dik.torrserverapi.server.ServerSettingsApi
import com.dik.torrserverapi.server.ServerSettingsApiImpl
import com.dik.torrserverapi.server.TorrentApi
import com.dik.torrserverapi.server.TorrentApiImpl
import com.dik.torrserverapi.server.TorrserverCommands
import com.dik.torrserverapi.server.TorrserverCommandsImpl
import com.dik.torrserverapi.server.TorrserverConfig
import com.dik.torrserverapi.server.TorrserverStuffApi
import com.dik.torrserverapi.server.TorrserverStuffApiImpl
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
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
    factoryOf(::DownloadFile).bind<DownloadFile>()
    factoryOf(::InstallTorrserver).bind<InstallTorrserver>()
    factoryOf(::BackupFile).bind<BackupFile>()
    factoryOf(::RestoreServerFromBackUp).bind<RestoreServerFromBackUp>()
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