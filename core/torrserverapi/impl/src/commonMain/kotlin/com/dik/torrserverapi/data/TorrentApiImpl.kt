package com.dik.torrserverapi.data

import com.dik.common.AppDispatchers
import com.dik.common.Result
import com.dik.common.utils.successResult
import com.dik.torrserverapi.TorrserverError
import com.dik.torrserverapi.data.errors.runCatchingKtor
import com.dik.torrserverapi.data.mappers.mapToTorrent
import com.dik.torrserverapi.data.mappers.mapToTorrentList
import com.dik.torrserverapi.data.mappers.mapToViewedList
import com.dik.torrserverapi.data.model.Body
import com.dik.torrserverapi.data.response.TorrentResponse
import com.dik.torrserverapi.data.response.ViewedReponse
import com.dik.torrserverapi.model.Torrent
import com.dik.torrserverapi.model.Viewed
import com.dik.torrserverapi.server.api.TorrentApi
import com.dik.torrserverapi.utils.fileToByteArray
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.timeout
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json

class TorrentApiImpl(
    private val client: HttpClient,
    private val dispatchers: AppDispatchers
) : TorrentApi {

    override suspend fun getTorrents(): Result<List<Torrent>, TorrserverError> {
        return runCatchingKtor {
            val body = Body(action = TorrentsAction.LIST.asString)
            val request = client.post("/torrents") {
                setBody(Json.encodeToString(Body.serializer(), body))
                contentType(ContentType.Application.Json)
            }

            val response = request.body<List<TorrentResponse>>()

            return Result.Success(response.mapToTorrentList())
        }
    }

    override suspend fun getTorrent(hash: String): Result<Torrent, TorrserverError> {
        return runCatchingKtor {
            val viewedList = getViewedList(hash).successResult() ?: emptyList()
            val body = Body(action = TorrentsAction.GET.asString, hash = hash)
            val request = client.post("/torrents") {
                setBody(Json.encodeToString(Body.serializer(), body))
                contentType(ContentType.Application.Json)
            }


            val response = request.body<TorrentResponse>()
            val torrent = response.mapToTorrent(viewedList)

            return Result.Success(torrent)
        }
    }

    override suspend fun addTorrent(filePath: String): Result<Torrent, TorrserverError> {
        return runCatchingKtor {
            val byteArray = fileToByteArray(filePath, dispatchers.ioDispatcher())

            val request = client.post("/torrent/upload") {
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append("save", true)
                            append(
                                "file", byteArray, Headers.build {
                                    append(HttpHeaders.ContentType, "application/x-bittorrent")
                                    append(
                                        HttpHeaders.ContentDisposition,
                                        "filename=new.torrent"
                                    )
                                }
                            )
                        }
                    )
                )

            }

            return if (request.status.isSuccess()) {
                val response = request.body<TorrentResponse>()

                Result.Success(response.mapToTorrent())
            } else {
                Result.Error(TorrserverError.Unknown("Response return error ${request.status.value}"))
            }
        }
    }

    override suspend fun updateTorrent(torrent: Torrent): Result<Unit, TorrserverError> {
        return runCatchingKtor {
            val body = Body(
                action = TorrentsAction.SET.asString,
                hash = torrent.hash,
                poster = torrent.poster,
                saveToDb = true,
            )
            val request = client.post("/torrents") {
                setBody(Json.encodeToString(Body.serializer(), body))
                contentType(ContentType.Application.Json)
            }

            if (request.status.isSuccess()) return Result.Success(Unit)

            return Result.Error(TorrserverError.HttpError.ResponseReturnError(request.status.description))
        }
    }

    override suspend fun getViewedList(hash: String): Result<List<Viewed>, TorrserverError> {
        return runCatchingKtor {
            val body = Body(action = TorrentsAction.LIST.asString, hash = hash)
            val request = client.post("/viewed") {
                setBody(Json.encodeToString(Body.serializer(), body))
                contentType(ContentType.Application.Json)
            }

            val resoponse = request.body<List<ViewedReponse>>()

            return Result.Success(resoponse.mapToViewedList())
        }
    }

    override suspend fun preloadTorrent(
        hash: String,
        fileIndex: Int
    ): Result<Unit, TorrserverError> {
        return runCatchingKtor {
            val request = client.get("/stream") {
                parameter("link", hash)
                parameter("index", fileIndex)
                parameter("preload", true)

                timeout {
                    requestTimeoutMillis = 60000 * 5
                    socketTimeoutMillis = 60000 * 5
                }
            }

            if (request.status.isSuccess()) return Result.Success(Unit)

            return Result.Error(
                TorrserverError.HttpError
                    .ResponseReturnError("Response return error: ${request.status.value}")
            )
        }
    }

    override suspend fun removeTorrent(hash: String): Result<Unit, TorrserverError> {
        return runCatchingKtor {
            try {
                val body = Body(action = TorrentsAction.REM.asString, hash = hash)
                val request = client.post("/torrents") {
                    setBody(Json.encodeToString(Body.serializer(), body))
                    contentType(ContentType.Application.Json)
                }

                if (request.status.isSuccess()) return Result.Success(Unit)

                return Result.Error(
                    TorrserverError.HttpError
                        .ResponseReturnError("Response return error: ${request.status.value}")
                )
            } catch (e: Exception) {
                return Result.Error(TorrserverError.Unknown(e.toString()))
            }
        }
    }
}