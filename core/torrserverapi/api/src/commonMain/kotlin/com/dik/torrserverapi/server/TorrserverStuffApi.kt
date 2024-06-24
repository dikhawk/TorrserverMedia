package com.dik.torrserverapi.server

import com.dik.common.Result
import com.dik.torrserverapi.TorrserverError

interface TorrserverStuffApi {

    /**
     * Tests whether server is alive or not
     * return String server version
     */
    suspend fun echo(): Result<String, TorrserverError>

    suspend fun startServer(): Result<String, TorrserverError>

    suspend fun stopServer(): Result<Unit, TorrserverError>

    suspend fun checkUpdates(): Result<String, TorrserverError>

    suspend fun downloadServer(): Result<String, TorrserverError>
}