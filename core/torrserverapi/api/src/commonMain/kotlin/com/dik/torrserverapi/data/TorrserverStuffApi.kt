package com.dik.torrserverapi.data

import com.dik.common.Result
import com.dik.torrserverapi.TorrserverError

interface TorrserverStuffApi {

    /**
     * Tests whether server is alive or not
     * return String server version
     */
    suspend fun echo(): Result<String, TorrserverError>
}