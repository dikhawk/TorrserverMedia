package com.dik.torrserverapi.server

import com.dik.common.Result
import com.dik.torrserverapi.TorrserverError

internal interface TorrserverRunner {
    suspend fun run(): Result<Unit, TorrserverError>
}

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal expect class TorrserverRunnerImpl: TorrserverRunner