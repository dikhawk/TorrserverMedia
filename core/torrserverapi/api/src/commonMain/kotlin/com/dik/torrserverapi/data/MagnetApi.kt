package com.dik.torrserverapi.data

import com.dik.common.Result
import com.dik.torrserverapi.TorrserverError

interface MagnetApi {
    suspend fun addMagnet(magnetUrl: String): Result<Unit, TorrserverError>
}