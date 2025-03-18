package com.dik.torrserverapi.server

import com.dik.torrserverapi.di.TorrserverDependencies
import com.dik.torrserverapi.di.dependencyModule
import com.dik.torrserverapi.di.httpModule
import com.dik.torrserverapi.di.platformModule
import com.dik.torrserverapi.di.torrserverModule
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngine
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.test.verify.verify

class KoinTest {

    @OptIn(KoinExperimentalAPI::class)
    @Test
    fun `Verify dependencies graph`() = runTest {
        val dependencies: TorrserverDependencies = mockk(relaxed = true)
        val modules = module {
            includes(
                torrserverModule,
                dependencyModule(dependencies),
                platformModule(),
                httpModule,
                module {
                    single<HttpClientEngine> { mockk(relaxed = true) }
                    single<HttpClientConfig<*>> { mockk(relaxed = true) }
                },
                platformSpecificModule()
            )
        }
        modules.verify()
    }
}

expect fun platformSpecificModule(): Module