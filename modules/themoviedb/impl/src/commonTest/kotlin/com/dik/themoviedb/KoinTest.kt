package com.dik.themoviedb

import com.dik.themoviedb.di.featureModule
import com.dik.themoviedb.di.httpModule
import com.dik.themoviedb.di.theMovieDbModule
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngine
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.module
import org.koin.test.verify.verify
import kotlin.test.Test

class KoinTest {

    @OptIn(KoinExperimentalAPI::class)
    @Test
    fun `Verify dependencies graph`() = runTest {
        val modules = module {
            includes(
                theMovieDbModule,
                httpModule,
                module {
                    single<HttpClientEngine> { mockk(relaxed = true) }
                    single<HttpClientConfig<*>> { mockk(relaxed = true) }
                },
                featureModule(mockk(relaxed = true))
            )
        }
        modules.verify()
    }
}