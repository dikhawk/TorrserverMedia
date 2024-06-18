package com.dik.torrserverapi

import com.dik.common.Result
import com.dik.torrserverapi.data.MagnetApi

class MagnetApiImpl: MagnetApi {
    override suspend fun addMagnet(magnetUrl: String): Result<Unit, TorrserverError> {
        TODO("Not yet implemented")
    }
}