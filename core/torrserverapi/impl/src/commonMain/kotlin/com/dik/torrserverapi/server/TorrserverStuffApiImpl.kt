package com.dik.torrserverapi.server

import com.dik.common.Result
import com.dik.torrserverapi.LOCAL_TORRENT_SERVER
import com.dik.torrserverapi.TorrserverError
import com.dik.torrserverapi.cmd.ServerCommands
import com.dik.torrserverapi.model.Release
import com.dik.torrserverapi.server.mappers.mapRelease
import com.dik.torrserverapi.server.response.ReleaseResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.HttpStatusCode

class TorrserverStuffApiImpl(
    private val serverCommands: ServerCommands, private val client: HttpClient
) : TorrserverStuffApi {

    override suspend fun echo(): Result<String, TorrserverError> {
        try {
            val request = client.get("$LOCAL_TORRENT_SERVER/echo")

            val result = request.body<String>()

            if (result.isNullOrEmpty()) return Result.Error(TorrserverError.Common.ResponseReturnNull)

            return Result.Success(result)
        } catch (e: Exception) {
            return Result.Error(TorrserverError.Common.Unknown(e.message ?: e.toString()))
        }
    }

    override suspend fun stopServer(): Result<Unit, TorrserverError> {
        try {
            val request = client.get("$LOCAL_TORRENT_SERVER/shutdown")

            if (request.status != HttpStatusCode.OK) {
                return Result.Error(
                    TorrserverError.Common.Unknown("Result return code: ${request.status.value}")
                )
            }

            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Error(TorrserverError.Common.Unknown(e.message ?: e.toString()))
        }
    }

    override suspend fun checkLatestRelease(): Result<Release, TorrserverError> {
        try {
            val request = client.get("https://api.github.com/repos/YouROK/TorrServer/releases") {
                parameter("per_page", 1)
            }

            val result = request.body<List<ReleaseResponse>>()

            if (result.isNullOrEmpty()) return Result.Error(TorrserverError.Common.ResponseReturnNull)

            return Result.Success(result.first().mapRelease())
        } catch (e: Exception) {
            return Result.Error(TorrserverError.Common.Unknown(e.message ?: e.toString()))
        }
    }

    override suspend fun downloadServer(url: String): Result<String, TorrserverError> {
        TODO("Not yet implemented")
    }
}


