package com.dik.torrserverapi.di

import com.dik.torrserverapi.cmd.CmdRunner
import com.dik.torrserverapi.cmd.KmpCmdRunner
import com.dik.torrserverapi.cmd.KmpServerCommands
import com.dik.torrserverapi.cmd.ServerCommands
import com.dik.torrserverapi.http.createHttpClient
import com.dik.torrserverapi.server.DownloadFile
import com.dik.torrserverapi.server.InstallTorrserver
import com.dik.torrserverapi.server.MagnetApi
import com.dik.torrserverapi.server.MagnetApiImpl
import com.dik.torrserverapi.server.TorrentApi
import com.dik.torrserverapi.server.TorrentApiImpl
import com.dik.torrserverapi.server.TorrserverCommands
import com.dik.torrserverapi.server.TorrserverCommandsImpl
import com.dik.torrserverapi.server.TorrserverStuffApi
import com.dik.torrserverapi.server.TorrserverStuffApiImpl
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val torrserverModule = module {
    factory<CmdRunner> { KmpCmdRunner }
    singleOf(::MagnetApiImpl).bind<MagnetApi>()
    singleOf(::TorrentApiImpl).bind<TorrentApi>()
    singleOf(::TorrserverStuffApiImpl).bind<TorrserverStuffApi>()
    single<ServerCommands> { KmpServerCommands }
    singleOf(::TorrserverCommandsImpl).bind<TorrserverCommands>()
    factoryOf(::DownloadFile).bind<DownloadFile>()
    factoryOf(::InstallTorrserver).bind<InstallTorrserver>()
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
                requestTimeoutMillis = 15000
                connectTimeoutMillis = 10000
                socketTimeoutMillis = 15000
            }

            install(HttpCache)
        }
    }
}