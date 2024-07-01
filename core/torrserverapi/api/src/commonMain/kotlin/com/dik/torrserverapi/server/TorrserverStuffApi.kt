package com.dik.torrserverapi.server

import com.dik.common.Result
import com.dik.torrserverapi.TorrserverError
import com.dik.torrserverapi.model.Release

interface TorrserverStuffApi {

    /**
     * Tests whether server is alive or not
     * return String server version
     */
    suspend fun echo(): Result<String, TorrserverError>

    suspend fun stopServer(): Result<Unit, TorrserverError>

    suspend fun checkLatestRelease(): Result<Release, TorrserverError>

    suspend fun downloadServer(url: String): Result<String, TorrserverError>
}