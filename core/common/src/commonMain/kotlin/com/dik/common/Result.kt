package com.dik.common

import com.dik.common.errors.Error
import kotlinx.coroutines.CancellationException

typealias AppError = Error

sealed class Result<out D, out E : AppError> {
    data class Success<out D, out E : AppError>(val data: D) : Result<D, E>()
    data class Error<out D, out E : AppError>(val error: E) : Result<D, E>()
}

sealed class ResultProgress<out D, out E : AppError> {
    data class Success<out D, out E : AppError>(val data: D) : ResultProgress<D, E>()
    data class Error<out D, out E : AppError>(val error: E) : ResultProgress<D, E>()
    data class Loading<out D, out E : AppError>(val progress: Progress) : ResultProgress<D, E>()
}

data class Progress(val progress: Double, val currentBytes: Long = 0, val totalBytes: Long = 0)

suspend inline fun <D, E : AppError>coRunCatching(error: (Exception) -> E, success: suspend () -> D): Result<D, E> {
    return try {
        Result.Success(success.invoke())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.Error(error.invoke(e))
    }
}

inline fun <D, E : AppError> Result<D, E>.onSuccess(block: (D) -> Unit): Result<D, E> {
    if (this is Result.Success) {
        block(this.data)
    }
    return this
}

inline fun <D, E : AppError> Result<D, E>.onError(block: (E) -> Unit): Result<D, E> {
    if (this is Result.Error) {
        block(this.error)
    }
    return this
}