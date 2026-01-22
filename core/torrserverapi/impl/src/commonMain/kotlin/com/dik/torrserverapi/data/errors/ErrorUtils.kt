package com.dik.torrserverapi.data.errors

import com.dik.common.Result
import com.dik.torrserverapi.TorrserverError
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import java.net.ConnectException
import kotlin.coroutines.cancellation.CancellationException

internal suspend inline fun <T> runCatchingKtor(block: suspend () -> T): Result<T, TorrserverError> {
    return try {
        Result.Success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: ConnectException) {
        Result.Error(TorrserverError.Server.NoServerConnection)
    } catch (e: ClientRequestException) { // 4xx ошибки
        Result.Error(
            TorrserverError.HttpError.ResponseReturnError(
                "Response return code: ${e.response.status.value}"
            )
        )
    } catch (e: ServerResponseException) { // 5xx ошибки
        Result.Error(
            TorrserverError.HttpError.ResponseReturnError(
                "Response return code: ${e.response.status.value}"
            )
        )
    } catch (e: Exception) {
        Result.Error(TorrserverError.Unknown(e.message ?: "Unknown error"))
    }
}