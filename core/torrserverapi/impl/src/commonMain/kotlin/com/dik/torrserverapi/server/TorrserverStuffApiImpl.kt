package com.dik.torrserverapi.server

import com.dik.common.AppDispatchers
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class TorrserverStuffApiImpl(
    private val serverCommands: ServerCommands, private val client: HttpClient,
    private val appDispatchers: AppDispatchers,
    private val scope: CoroutineScope
) : TorrserverStuffApi {

    private val serverStatus = MutableSharedFlow<Result<String, TorrserverError>>()

    init {
        scope.launch { startObservationServerStatus(1000) }
    }

    override suspend fun echo(): Result<String, TorrserverError> {
        try {
            val request = withContext(appDispatchers.ioDispatcher()) {
                client.get("$LOCAL_TORRENT_SERVER/echo")
            }

            val result = request.body<String>()

            if (result.isNullOrEmpty()) return Result.Error(TorrserverError.Common.ResponseReturnNull)

            return Result.Success(result)
        } catch (e: Exception) {
            return Result.Error(TorrserverError.Common.Unknown(e.message ?: e.toString()))
        }
    }

    private suspend fun startObservationServerStatus(delay: Long): Result<Unit, TorrserverError> {
        while (true) {
            val echo = echo()

            serverStatus.emit(echo)

            delay(delay)
        }
    }

    override fun observerServerStatus(): SharedFlow<Result<String, TorrserverError>> = serverStatus.asSharedFlow()

    override suspend fun stopServer(): Result<Unit, TorrserverError> {
        try {
            val request = withContext(appDispatchers.ioDispatcher()) {
                client.get("$LOCAL_TORRENT_SERVER/shutdown")
            }

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
            val request = withContext(appDispatchers.ioDispatcher()) {
                client.get("https://api.github.com/repos/YouROK/TorrServer/releases") {
                    parameter("per_page", 1)
                }
            }

            val result = request.body<List<ReleaseResponse>>()

            if (result.isNullOrEmpty()) return Result.Error(TorrserverError.Common.ResponseReturnNull)

            return Result.Success(result.first().mapRelease())
        } catch (e: Exception) {
            return Result.Error(TorrserverError.Common.Unknown(e.message ?: e.toString()))
        }
    }
}


