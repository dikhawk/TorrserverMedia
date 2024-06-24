package com.dik.torrserverapi.server

import com.dik.common.Result
import com.dik.torrserverapi.Torrent
import com.dik.torrserverapi.TorrserverError

interface TorrentApi {
    suspend fun getTorrentsList(): Result<List<Torrent>, TorrserverError>

    suspend fun addTorrent(torrent: Torrent): Result<Unit, TorrserverError>
}