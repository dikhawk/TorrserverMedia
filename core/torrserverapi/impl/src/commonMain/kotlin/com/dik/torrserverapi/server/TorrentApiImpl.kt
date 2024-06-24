package com.dik.torrserverapi.server

import com.dik.common.Result
import com.dik.torrserverapi.Torrent
import com.dik.torrserverapi.TorrserverError

class TorrentApiImpl: TorrentApi {
    override suspend fun getTorrentsList(): Result<List<Torrent>, TorrserverError> {
        TODO("Not yet implemented")
    }

    override suspend fun addTorrent(torrent: Torrent): Result<Unit, TorrserverError> {
        TODO("Not yet implemented")
    }
}