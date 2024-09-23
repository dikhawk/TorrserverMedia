package com.dik.themoviedb.di

import com.dik.themoviedb.constants.MOVIEDB_API_URL
import com.dik.themoviedb.impl.BuildKonfig
import com.dik.themoviedb.response.PolymorphContentResponse
import com.dik.themoviedb.response.MoviePolymorphResponse
import com.dik.themoviedb.response.PersonPolymorphResponse
import com.dik.themoviedb.response.TvShowPolymorphResponse
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.appendIfNameAbsent
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import org.koin.dsl.module

internal val httpModule = module {
    single<HttpClient> {
        createHttpClient().config {
            defaultRequest {
                url(MOVIEDB_API_URL)
                headers.appendIfNameAbsent(HttpHeaders.Authorization, "Bearer " + BuildKonfig.MOVIEDB_API)
                headers.appendIfNameAbsent(HttpHeaders.ContentType, "application/json")
            }
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                        serializersModule = SerializersModule {
                            polymorphic(PolymorphContentResponse::class) {
                                subclass(MoviePolymorphResponse::class)
                                subclass(PersonPolymorphResponse::class)
                                subclass(TvShowPolymorphResponse::class)
                            }
                        }
                    }
                )
            }

            install(HttpTimeout) {
                requestTimeoutMillis = 30000
                connectTimeoutMillis = 30000
                socketTimeoutMillis = 30000
            }

            install(HttpCache)
        }
    }
}

expect fun createHttpClient(): HttpClient