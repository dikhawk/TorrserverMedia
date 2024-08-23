package com.dik.torrserverapi.server

import com.dik.common.Result
import com.dik.torrserverapi.model.Torrent
import com.dik.torrserverapi.TorrserverError
import com.dik.torrserverapi.model.Viewed

interface TorrentApi {

    suspend fun getTorrents(): Result<List<Torrent>, TorrserverError>

    suspend fun getTorrent(hash: String): Result<Torrent, TorrserverError>

    suspend fun addTorrent(filePath: String): Result<Torrent, TorrserverError>

    suspend fun upateTorrent(hash: String): Result<Torrent, TorrserverError>

    suspend fun getViewedList(hash: String): Result<List<Viewed>, TorrserverError>
}