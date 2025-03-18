package com.dik.settings.main

import com.dik.settings.di.settingListModule
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.test.verify.verify

class KoinTest {

    @OptIn(KoinExperimentalAPI::class)
    @Test
    fun `Verify dependencies graph`() = runTest {
        settingListModule(mockk(relaxed = true)).verify()
    }
}