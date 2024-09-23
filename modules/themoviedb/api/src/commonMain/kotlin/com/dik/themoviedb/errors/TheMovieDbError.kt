package com.dik.themoviedb.errors

import com.dik.common.errors.Error

sealed interface TheMovieDbError: Error {

    sealed interface HttpError: TheMovieDbError {
        data object ResponseReturnNull: HttpError
        data class ResponseReturnError(val message: String): HttpError
    }

    data class Unknown(val message: String): TheMovieDbError
}