package com.dik.torrserverapi.server

import com.dik.common.Result
import com.dik.torrserverapi.TorrserverError

class MagnetApiImpl: MagnetApi {
    override suspend fun addMagnet(magnetUrl: String): Result<Unit, TorrserverError> {
        TODO("Not yet implemented")
    }
}