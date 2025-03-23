package com.dik.common.utils

import com.dik.common.Result
import com.dik.common.errors.Error
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.delay

suspend fun <T, E : Error> Deferred<Result<T, E>>.repeatIfError(tries: Int = 3, delay: Long = 1000L): Result<T, E> {
    for (t in 1..tries) {
        when (val result = await()) {
            is Result.Error -> return result
            is Result.Success -> if (t == tries) return result
        }
        delay(delay)
    }

    throw UnsupportedOperationException()
}

suspend fun <T> (suspend () -> T).repeatIf(
    maxTries: Int = 15, delay: Long = 1000L, predicate: (T) -> Boolean
): T? {
    for (t in 1..maxTries) {
        val result = invoke()
        if (!predicate.invoke(result)) {
            return result
        }

        delay(delay)
    }

    return null
}

fun <T, E : Error> Result<T, E>.successResult(handleError: (error: E) -> Unit = {}): T? {
    return when (this) {
        is Result.Error -> {
            handleError(this.error)
            null
        }

        is Result.Success -> data
    }
}