package com.dik.torrserverapi.server

import com.dik.common.AppDispatchers
import com.dik.common.Result
import com.dik.torrserverapi.LOCAL_TORRENT_SERVER
import com.dik.torrserverapi.TorrserverError
import com.dik.torrserverapi.model.Torrent
import com.dik.torrserverapi.server.mappers.mapToTorrent
import com.dik.torrserverapi.server.model.Body
import com.dik.torrserverapi.server.response.TorrentResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.withContext

class MagnetApiImpl(
    private val client: HttpClient,
    private val dispatchers: AppDispatchers
) : MagnetApi {

    override suspend fun addMagnet(magnetUrl: String): Result<Torrent, TorrserverError> {
        try {
            val body = Body(action = TorrentsAction.ADD.asString, link = magnetUrl, saveToDb = true)
            val request = withContext(dispatchers.ioDispatcher()) {
                client.post("$LOCAL_TORRENT_SERVER/torrents") {
                    setBody(body)
                    contentType(ContentType.Application.Json)
                }
            }

            val response = request.body<TorrentResponse>()

            return Result.Success(response.mapToTorrent())
        } catch (e: Exception) {
            return Result.Error(TorrserverError.Common.Unknown(e.toString()))
        }
    }
}