package com.dik.torrserverapi.server.api

import com.dik.common.Result
import com.dik.torrserverapi.TorrserverError
import com.dik.torrserverapi.model.Torrent
import com.dik.torrserverapi.model.Viewed
import kotlinx.coroutines.flow.Flow

interface TorrentApi {

    fun observeTorrents(interval: Long = 5000): Flow<Result<List<Torrent>, TorrserverError>>

    suspend fun getTorrents(): Result<List<Torrent>, TorrserverError>

    suspend fun getTorrent(hash: String): Result<Torrent, TorrserverError>

    suspend fun addTorrent(filePath: String): Result<Torrent, TorrserverError>

    suspend fun updateTorrent(torrent: Torrent): Result<Unit, TorrserverError>

    suspend fun getViewedList(hash: String): Result<List<Viewed>, TorrserverError>

    suspend fun preloadTorrent(hash: String, fileIndex: Int): Result<Unit, TorrserverError>

    suspend fun removeTorrent(hash: String): Result<Unit, TorrserverError>

    suspend fun addMagnet(magnetUrl: String): Result<Torrent, TorrserverError>
}