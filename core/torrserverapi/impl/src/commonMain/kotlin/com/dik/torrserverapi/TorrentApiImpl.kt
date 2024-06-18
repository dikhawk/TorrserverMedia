package com.dik.torrserverapi

import com.dik.common.Result
import com.dik.torrserverapi.data.TorrentApi

class TorrentApiImpl: TorrentApi {
    override suspend fun getTorrentsList(): Result<List<Torrent>, TorrserverError> {
        TODO("Not yet implemented")
    }

    override suspend fun addTorrent(torrent: Torrent): Result<Unit, TorrserverError> {
        TODO("Not yet implemented")
    }
}