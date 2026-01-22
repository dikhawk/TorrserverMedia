package com.dik.torrserverapi.data

import com.dik.common.AppDispatchers
import com.dik.common.Result
import com.dik.torrserverapi.TorrserverError
import com.dik.torrserverapi.data.errors.runCatchingKtor
import com.dik.torrserverapi.data.mappers.mapToServerSettings
import com.dik.torrserverapi.data.mappers.mapToSettings
import com.dik.torrserverapi.data.model.Body
import com.dik.torrserverapi.data.response.SettingsResponse
import com.dik.torrserverapi.model.ServerSettings
import com.dik.torrserverapi.server.api.ServerSettingsApi
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

    override suspend fun saveSettings(settings: ServerSettings): Result<ServerSettings, TorrserverError> {
        return runCatchingKtor {
            val body =
                Body(action = TorrentsAction.SET.asString, settings = settings.mapToSettings())
            val request = withContext(dispatchers.ioDispatcher()) {
                client.post("/settings") {
                    setBody(body)
                    contentType(ContentType.Application.Json)
                }
            }

            if (request.status.isSuccess()) {
                return getSettings()
            } else {
                return Result.Error(TorrserverError.Unknown("Settings not applied"))
            }
        }
    }

    override suspend fun getSettings(): Result<ServerSettings, TorrserverError> {
        return runCatchingKtor {
            val body = Body(action = TorrentsAction.GET.asString)
            val request = withContext(dispatchers.ioDispatcher()) {
                client.post("/settings") {
                    setBody(body)
                    contentType(ContentType.Application.Json)
                }
            }

            val response = request.body<SettingsResponse>()

            return Result.Success(response.mapToServerSettings())
        }
    }

    override suspend fun defaultSettings(): Result<ServerSettings, TorrserverError> {
        return runCatchingKtor {
            val body = Body(action = TorrentsAction.DEFAULT.asString)
            val request = withContext(dispatchers.ioDispatcher()) {
                client.post("/settings") {
                    setBody(body)
                    contentType(ContentType.Application.Json)
                }
            }

            if (request.status.isSuccess()) {
                return getSettings()
            } else {
                return Result.Error(TorrserverError.Unknown("Default settings not applied"))
            }
        }
    }
}