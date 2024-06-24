package com.dik.torrserverapi.di

import com.dik.torrserverapi.cmd.CmdRunner
import com.dik.torrserverapi.cmd.KmpCmdRunner
import com.dik.torrserverapi.cmd.KmpServerCommands
import com.dik.torrserverapi.cmd.ServerCommands
import com.dik.torrserverapi.http.createHttpClient
import com.dik.torrserverapi.server.MagnetApi
import com.dik.torrserverapi.server.MagnetApiImpl
import com.dik.torrserverapi.server.TorrentApi
import com.dik.torrserverapi.server.TorrentApiImpl
import com.dik.torrserverapi.server.TorrserverStuffApi
import com.dik.torrserverapi.server.TorrserverStuffApiImpl
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

internal val torrserverModule = module {
    factory<CmdRunner> { KmpCmdRunner }
    single<MagnetApi> { MagnetApiImpl() }
    single<TorrentApi> { TorrentApiImpl() }
    single<TorrserverStuffApi> { TorrserverStuffApiImpl(get(), get()) }
    single<ServerCommands> { KmpServerCommands }
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