package com.dik.torrserverapi.data

import com.dik.common.Result
import com.dik.torrserverapi.TorrserverError
import com.dik.torrserverapi.data.errors.runCatchingKtor
import com.dik.torrserverapi.data.mappers.mapToTorrent
import com.dik.torrserverapi.data.model.Body
import com.dik.torrserverapi.data.response.TorrentResponse
import com.dik.torrserverapi.model.Torrent
import com.dik.torrserverapi.server.api.MagnetApi
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess

internal class MagnetApiImpl(
    private val client: HttpClient,
) : MagnetApi {

    override suspend fun addMagnet(magnetUrl: String): Result<Torrent, TorrserverError> {
        return runCatchingKtor {
            val body = Body(action = TorrentsAction.ADD.asString, link = magnetUrl, saveToDb = true)
            val response =
                client.post("/torrents") {
                    setBody(body)
                    contentType(ContentType.Application.Json)
                }

            if (!response.status.isSuccess()) {
                return Result.Error(TorrserverError.HttpError.ResponseReturnError(response.status.description))
            }

            response.body<TorrentResponse>().mapToTorrent()
        }
    }

}