package com.dik.torrserverapi.server

import com.dik.common.AppDispatchers
import com.dik.common.Result
import com.dik.torrserverapi.LOCAL_TORRENT_SERVER
import com.dik.torrserverapi.TorrserverError
import com.dik.torrserverapi.model.Torrent
import com.dik.torrserverapi.server.mappers.mapToTorrent
import com.dik.torrserverapi.server.mappers.mapToTorrentList
import com.dik.torrserverapi.server.response.TorrentResponse
import com.dik.torrserverapi.utils.fileToByteArray
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
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

            val resoponse = request.body<List<TorrentResponse>>()

            return Result.Success(resoponse.mapToTorrentList())
        } catch (e: Exception) {
            return Result.Error(TorrserverError.Common.Unknown(e.toString()))
        }
    }

    override suspend fun getTorrent(hash: String): Result<Torrent, TorrserverError> {
        try {
            val body = Body(action = TorrentsAction.GET.asString, hash = hash)
            val request = withContext(dispatchers.ioDispatcher()) {
                client.post("$LOCAL_TORRENT_SERVER/torrents") {
                    setBody(Json.encodeToString(Body.serializer(), body))
                    contentType(ContentType.Application.Json)
                }
            }

            val response = request.body<TorrentResponse>()

            return Result.Success(response.mapToTorrent())
        } catch (e: Exception) {
            return Result.Error(TorrserverError.Common.Unknown(e.toString()))
        }
    }

    override suspend fun addTorrent(filePath: String): Result<Torrent, TorrserverError> {
        try {
            val byteArray = filePath.fileToByteArray()

            val request = withContext(dispatchers.ioDispatcher()) {
                client.post("$LOCAL_TORRENT_SERVER/torrent/upload") {
                    setBody(MultiPartFormDataContent(
                        formData {
                            append("save", true)
                            append("file", byteArray, Headers.build {
                                append(HttpHeaders.ContentType, "application/x-bittorrent")
                                append(HttpHeaders.ContentDisposition, "filename=new.torrent")
                            })
                        }
                    ))
                }
            }

            return if (request.status.isSuccess()) {
                val response = request.body<TorrentResponse>()

                Result.Success(response.mapToTorrent())
            } else {
                Result.Error(TorrserverError.Common.Unknown("Response return error ${request.status.value}"))
            }
        } catch (e: Exception) {
            return Result.Error(TorrserverError.Common.Unknown(e.toString()))
        }
    }

    override suspend fun upateTorrent(hash: String): Result<Torrent, TorrserverError> {
        TODO("Not yet implemented")
//        url http://127.0.0.1:8090/torrents
//        body
//        {
//            "action": "set",
//            "hash": "1740f609785866e6f1137ae87450559163b977d0",
//            "title": "House of the Dragon 1 - LostFilm.TV [1080p]",
//            "poster": "https://masterpiecer-images.s3.yandex.net/5fd531dca6427c7:upscaled",
//            "category": ""
//        }
    }
}