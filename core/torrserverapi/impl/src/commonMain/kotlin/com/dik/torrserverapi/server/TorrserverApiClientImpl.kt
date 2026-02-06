package com.dik.torrserverapi.server

import com.dik.common.Result
import com.dik.torrserverapi.TorrserverError
import com.dik.torrserverapi.data.errors.runCatchingKtor
import com.dik.torrserverapi.data.mappers.mapRelease
import com.dik.torrserverapi.data.response.ReleaseResponse
import com.dik.torrserverapi.model.Release
import com.dik.torrserverapi.server.api.TorrserverApiClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class TorrserverApiClientImpl(
    private val client: HttpClient,
) : TorrserverApiClient {

    override suspend fun echo(): Result<String, TorrserverError> {
        return runCatchingKtor {
            val request = client.get("/echo")
            request.body<String>()
        }
    }

    override suspend fun stopServer(): Result<Unit, TorrserverError> {
        return runCatchingKtor {
            client.get("/shutdown")
        }
    }

    override suspend fun checkLatestRelease(): Result<Release, TorrserverError> {
        return runCatchingKtor {
            val request = client.get("https://api.github.com/repos/YouROK/TorrServer/releases") {
                parameter("per_page", 1)
            }
            val result = request.body<List<ReleaseResponse>>()

            result.first().mapRelease()
        }
    }
}


