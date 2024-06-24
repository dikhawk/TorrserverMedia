package com.dik.torrserverapi.server

import com.dik.common.Result
import com.dik.torrserverapi.TorrserverError
import com.dik.torrserverapi.cmd.ServerCommands
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.request
import org.koin.mp.KoinPlatformTools

class TorrserverStuffApiImpl(
    private val serverCommands: ServerCommands,
    private val client: HttpClient
) : TorrserverStuffApi {
    override suspend fun echo(): Result<String, TorrserverError> {
        TODO("Not yet implemented")
    }

    override suspend fun startServer(): Result<String, TorrserverError> {
        try {
            serverCommands.startServer("TorrServer-linux-amd64", "/home/dik/TorrServer")

            return Result.Success("Server message not init")
        } catch (e: Exception) {
            return Result.Error(TorrserverError.Common.Unknown(e.message ?: ""))
        }
    }

    override suspend fun stopServer(): Result<Unit, TorrserverError> {
        TODO("Not yet implemented")
    }

    override suspend fun checkUpdates(): Result<String, TorrserverError> {
        val request = client.get("https://api.github.com/repos/YouROK/TorrServer/releases") {
            parameter("per_page", 1)
        }

        val response = request.body<String>()

        return Result.Success(response)
    }

    override suspend fun downloadServer(): Result<String, TorrserverError> {
        TODO("Not yet implemented")
    }
}