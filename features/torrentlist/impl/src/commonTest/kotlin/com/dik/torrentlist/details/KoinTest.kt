package com.dik.torrentlist.details

import com.dik.torrentlist.di.TorrentListDependencies
import com.dik.torrentlist.di.platformModule
import com.dik.torrentlist.di.torrentListModule
import com.dik.torrentlist.di.useCasesModule
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.module
import org.koin.test.verify.verify

class KoinTest {

    @OptIn(KoinExperimentalAPI::class)
    @Test
    fun verifyModule() = runTest {
        val dependencies: TorrentListDependencies = mockk(relaxed = true)
        val m = module {
            includes(
                platformModule(),
                torrentListModule(dependencies),
                useCasesModule(),
                platformModule(dependencies)
            )
        }

        m.verify()
    }


}