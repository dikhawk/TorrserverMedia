package com.dik.torrserverapi

import com.dik.common.Result
import com.dik.torrserverapi.data.TorrserverStuffApi

class TorrserverStuffApiImpl: TorrserverStuffApi {
    override suspend fun echo(): Result<String, TorrserverError> {
        TODO("Not yet implemented")
    }
}