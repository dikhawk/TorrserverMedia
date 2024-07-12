package com.dik.torrserverapi.server

import com.dik.common.Result
import com.dik.torrserverapi.TorrserverError
import com.dik.torrserverapi.model.Torrent

interface MagnetApi {
    suspend fun addMagnet(magnetUrl: String): Result<Torrent, TorrserverError>
}