package com.dik.torrserverapi.server

import com.dik.common.AppDispatchers
import com.dik.common.Result
import com.dik.common.utils.successResult
import com.dik.torrserverapi.LOCAL_TORRENT_SERVER
import com.dik.torrserverapi.TorrserverError
import com.dik.torrserverapi.model.Torrent
import com.dik.torrserverapi.model.Viewed
import com.dik.torrserverapi.server.mappers.mapToTorrent
import com.dik.torrserverapi.server.mappers.mapToTorrentList
import com.dik.torrserverapi.server.mappers.mapToViewedList
import com.dik.torrserverapi.server.model.Body
import com.dik.torrserverapi.server.response.TorrentResponse
import com.dik.torrserverapi.server.response.ViewedReponse
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
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class TorrentApiImpl(
    private val client: HttpClient,
    private val dispatchers: AppDispatchers
) : TorrentApi {

    override suspend fun getTorrents(): Result<List<Torrent>, TorrserverError> {
        try {
            val body = Body(action = TorrentsAction.LIST.asString)
            val request = withContext(dispatchers.ioDispatcher()) {
                client.post("$LOCAL_TORRENT_SERVER/torrents") {
                    setBody(Json.encodeToString(Body.serializer(), body))
                    contentType(ContentType.Application.Json)
                }
            }

            val response = request.body<List<TorrentResponse>>()

            return Result.Success(response.mapToTorrentList())
        } catch (e: Exception) {
            return Result.Error(TorrserverError.Unknown(e.toString()))
        }
    }

    override suspend fun getTorrent(hash: String): Result<Torrent, TorrserverError> {
        try {
            val viewedList = getViewedList(hash).successResult() ?: emptyList()
            val body = Body(action = TorrentsAction.GET.asString, hash = hash)
            val request = withContext(dispatchers.ioDispatcher()) {
                client.post("$LOCAL_TORRENT_SERVER/torrents") {
                    setBody(Json.encodeToString(Body.serializer(), body))
                    contentType(ContentType.Application.Json)
                }
            }

            val response = request.body<TorrentResponse>()
            val torrent = response.mapToTorrent(viewedList)

            return Result.Success(torrent)
        } catch (e: Exception) {
            return Result.Error(TorrserverError.Unknown(e.toString()))
        }
    }

    override suspend fun addTorrent(filePath: String): Result<Torrent, TorrserverError> {
        try {
            val byteArray = fileToByteArray(filePath, dispatchers.ioDispatcher())

            val request = withContext(dispatchers.ioDispatcher()) {
                client.post("$LOCAL_TORRENT_SERVER/torrent/upload") {
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
            }

            return if (request.status.isSuccess()) {
                val response = request.body<TorrentResponse>()

                Result.Success(response.mapToTorrent())
            } else {
                Result.Error(TorrserverError.Unknown("Response return error ${request.status.value}"))
            }
        } catch (e: Exception) {
            return Result.Error(TorrserverError.Unknown(e.toString()))
        }
    }

    override suspend fun updateTorrent(torrent: Torrent): Result<Unit, TorrserverError> {
        try {
            val body = Body(
                action = TorrentsAction.SET.asString,
                hash = torrent.hash,
                poster = torrent.poster,
                saveToDb = true,
            )
            val request = withContext(dispatchers.ioDispatcher()) {
                client.post("$LOCAL_TORRENT_SERVER/torrents") {
                    setBody(Json.encodeToString(Body.serializer(), body))
                    contentType(ContentType.Application.Json)
                }
            }

            if (request.status.isSuccess()) return Result.Success(Unit)

            return Result.Error(TorrserverError.HttpError.ResponseReturnError(request.status.description))
        } catch (e: Exception) {
            return Result.Error(TorrserverError.Unknown(e.toString()))
        }
    }

    override suspend fun getViewedList(hash: String): Result<List<Viewed>, TorrserverError> {
        try {
            val body = Body(action = TorrentsAction.LIST.asString, hash = hash)
            val request = withContext(dispatchers.ioDispatcher()) {
                client.post("$LOCAL_TORRENT_SERVER/viewed") {
                    setBody(Json.encodeToString(Body.serializer(), body))
                    contentType(ContentType.Application.Json)
                }
            }

            val resoponse = request.body<List<ViewedReponse>>()

            return Result.Success(resoponse.mapToViewedList())
        } catch (e: Exception) {
            return Result.Error(TorrserverError.Unknown(e.toString()))
        }
    }

    override suspend fun preloadTorrent(
        hash: String,
        fileIndex: Int
    ): Result<Unit, TorrserverError> {
        try {
            val request = withContext(dispatchers.ioDispatcher()) {
                client.get("$LOCAL_TORRENT_SERVER/stream") {
                    parameter("link", hash)
                    parameter("index", fileIndex)
                    parameter("preload", true)

                    timeout {
                        requestTimeoutMillis = 60000 * 5
                        socketTimeoutMillis = 60000 * 5
                    }
                }
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

    override suspend fun removeTorrent(hash: String): Result<Unit, TorrserverError> {
        try {
            try {
                val viewedList = getViewedList(hash).successResult() ?: emptyList()
                val body = Body(action = TorrentsAction.REM.asString, hash = hash)
                val request = withContext(dispatchers.ioDispatcher()) {
                    client.post("$LOCAL_TORRENT_SERVER/torrents") {
                        setBody(Json.encodeToString(Body.serializer(), body))
                        contentType(ContentType.Application.Json)
                    }
                }

                if (request.status.isSuccess()) return Result.Success(Unit)

                return Result.Error(
                    TorrserverError.HttpError
                        .ResponseReturnError("Response return error: ${request.status.value}")
                )
            } catch (e: Exception) {
                return Result.Error(TorrserverError.Unknown(e.toString()))
            }
        } catch (e: Exception) {
            return Result.Error(TorrserverError.Unknown(e.toString()))
        }
    }
}