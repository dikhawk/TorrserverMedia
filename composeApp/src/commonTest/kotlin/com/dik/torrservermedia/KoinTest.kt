package com.dik.torrservermedia

import com.dik.torrservermedia.di.appModule
import com.dik.torrservermedia.di.appSettingsModule
import com.dik.torrservermedia.di.commonModule
import com.dik.torrservermedia.di.featuresModule
import com.dik.torrservermedia.di.platformEventsModule
import com.dik.torrservermedia.di.platformModule
import com.dik.torrservermedia.di.theMovieDbApiModule
import com.dik.torrservermedia.di.torrserverModule
import com.dik.torrservermedia.di.windowAdaptiveModule
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.module
import org.koin.test.verify.verify

class KoinTest {

    @OptIn(KoinExperimentalAPI::class)
    @Test
    fun `Verify dependencies graph`() = runTest {
        val modules = module {
            includes(
                appModule,
                appSettingsModule,
                featuresModule,
                torrserverModule,
                theMovieDbApiModule,
                platformModule(),
                platformEventsModule(),
                windowAdaptiveModule,
                commonModule
            )
        }
        modules.verify()
    }
}