package com.dik.torrserverapi.server

import com.dik.common.AppDispatchers
import com.dik.common.Result
import com.dik.torrserverapi.LOCAL_TORRENT_SERVER
import com.dik.torrserverapi.TorrserverError
import com.dik.torrserverapi.model.ServerSettings
import com.dik.torrserverapi.server.mappers.mapToServerSettings
import com.dik.torrserverapi.server.mappers.mapToSettings
import com.dik.torrserverapi.server.model.Body
import com.dik.torrserverapi.server.response.SettingsResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.withContext

class ServerSettingsApiImpl(
    private val client: HttpClient,
    private val dispatchers: AppDispatchers
) : ServerSettingsApi {

    suspend override fun saveSettings(settings: ServerSettings): Result<ServerSettings, TorrserverError> {
        try {
            val body = Body(action = TorrentsAction.SET.asString, settings = settings.mapToSettings())
            val request = withContext(dispatchers.ioDispatcher()) {
                client.post("$LOCAL_TORRENT_SERVER/settings") {
                    setBody(body)
                    contentType(ContentType.Application.Json)
                }
            }

            if (request.status.isSuccess()) {
                return getSettings()
            } else {
                return Result.Error(TorrserverError.Common.Unknown("Settings not applied"))
            }
        } catch (e: Exception) {
            return Result.Error(TorrserverError.Common.Unknown(e.toString()))
        }
    }

    suspend override fun getSettings(): Result<ServerSettings, TorrserverError> {
        try {
            val body = Body(action = TorrentsAction.GET.asString)
            val request = withContext(dispatchers.ioDispatcher()) {
                client.post("$LOCAL_TORRENT_SERVER/settings") {
                    setBody(body)
                    contentType(ContentType.Application.Json)
                }
            }

            val response = request.body<SettingsResponse>()

            return Result.Success(response.mapToServerSettings())
        } catch (e: Exception) {
            return Result.Error(TorrserverError.Common.Unknown(e.toString()))
        }
    }

    suspend override fun defaultSettings(): Result<ServerSettings, TorrserverError> {
        try {
            val body = Body(action = TorrentsAction.DEFAULT.asString)
            val request = withContext(dispatchers.ioDispatcher()) {
                client.post("$LOCAL_TORRENT_SERVER/settings") {
                    setBody(body)
                    contentType(ContentType.Application.Json)
                }
            }

            if (request.status.isSuccess()) {
                return getSettings()
            } else {
                return Result.Error(TorrserverError.Common.Unknown("Default settings not applied"))
            }
        } catch (e: Exception) {
            return Result.Error(TorrserverError.Common.Unknown(e.toString()))
        }
    }
}