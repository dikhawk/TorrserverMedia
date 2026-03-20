package com.dik.themoviedb.utils

import com.dik.common.Result
import com.dik.themoviedb.errors.TheMovieDbError
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import java.net.ConnectException
import kotlin.coroutines.cancellation.CancellationException

internal suspend inline fun <T> runCatchingKtor(block: suspend () -> T): Result<T, TheMovieDbError> {
    return try {
        Result.Success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: ConnectException) {
        Result.Error(TheMovieDbError.Server.NoServerConnection)
    } catch (e: ClientRequestException) { // 4xx ошибки
        Result.Error(
            TheMovieDbError.HttpError.ResponseReturnError(
                "Response return code: ${e.response.status.value}"
            )
        )
    } catch (e: ServerResponseException) { // 5xx ошибки
        Result.Error(
            TheMovieDbError.HttpError.ResponseReturnError(
                "Response return code: ${e.response.status.value}"
            )
        )
    } catch (e: Exception) {
        Result.Error(TheMovieDbError.Unknown(e.message ?: "Unknown error"))
    }
}