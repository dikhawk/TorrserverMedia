package com.dik.torrserverapi.server

import com.dik.common.Result
import com.dik.torrserverapi.LOCAL_TORRENT_SERVER
import com.dik.torrserverapi.model.Torrent
import com.dik.torrserverapi.TorrserverError
import com.dik.torrserverapi.server.mappers.mapToTorrent
import com.dik.torrserverapi.server.mappers.mapToTorrentList
import com.dik.torrserverapi.server.response.TorrentResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json

class TorrentApiImpl(
    private val client: HttpClient
) : TorrentApi {

    override suspend fun getTorrents(): Result<List<Torrent>, TorrserverError> {
        try {
            val body = Body(action = TorrentsAction.LIST.asString)
            val request = client.post("$LOCAL_TORRENT_SERVER/torrents") {
                setBody(Json.encodeToString(Body.serializer(), body))
                contentType(ContentType.Application.Json)
            }

            val resoponse = request.body<List<TorrentResponse>>()

            return Result.Success(resoponse.mapToTorrentList())
        } catch (e: Exception) {
            return Result.Error(TorrserverError.Common.Unknown(e.toString()))
        }
    }

    override suspend fun getTorrent(hash: String): Result<Torrent, TorrserverError> {
        try {
            val body  = Body(action  = TorrentsAction.GET.asString, hash = hash)
            val request = client.post("$LOCAL_TORRENT_SERVER/torrents") {
                setBody(Json.encodeToString(Body.serializer(), body))
                contentType(ContentType.Application.Json)
            }

            val response  = request.body<TorrentResponse>()

            return Result.Success(response.mapToTorrent())
        } catch  (e: Exception)  {
            return Result.Error(TorrserverError.Common.Unknown(e.toString()))
        }
    }

    override suspend fun addTorrent(torrent: Torrent): Result<Unit, TorrserverError> {
        TODO("Not yet implemented")
    }
}