package com.dik.torrserverapi.server

import com.dik.common.Result
import com.dik.torrserverapi.TorrserverError

internal interface TorrserverRunner {
    suspend fun run(): Result<Unit, TorrserverError>
}