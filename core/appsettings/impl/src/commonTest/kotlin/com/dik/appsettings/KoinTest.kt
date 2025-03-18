package com.dik.appsettings

import com.dik.appsettings.impl.di.AppSettingsDependencies
import com.dik.appsettings.impl.di.appSettingModule
import com.dik.appsettings.impl.di.commonModule
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.module
import org.koin.test.verify.verify

class KoinTest {

    @OptIn(KoinExperimentalAPI::class)
    @Test
    fun `Verify dependencies graph`() = runTest {
        val dependencies: AppSettingsDependencies = mockk(relaxed = true)
        val modules = module {
            includes(
                appSettingModule(dependencies),
                commonModule(dependencies)
            )
        }
        modules.verify()
    }
}