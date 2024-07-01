package com.dik.common

import com.dik.common.errors.Error

typealias AppError = Error

sealed class Result<out D, out E : AppError> {
    data class Success<out D, out E : AppError>(val data: D) : Result<D, E>()
    data class Error<out D, out E : AppError>(val error: E) : Result<D, E>()
    data class Loading<out D, out E : AppError>(val progress: Progress) : Result<D, E>()
}

data class Progress(val progress: Double, val currentBytes: Long = 0, val totalBytes: Long = 0)