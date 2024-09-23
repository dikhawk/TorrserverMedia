package com.dik.torrserverapi

import com.dik.common.errors.Error

sealed interface TorrserverError : Error {

    sealed interface HttpError: TorrserverError {
        data object ResponseReturnNull: HttpError
        data class ResponseReturnError(val message: String): HttpError
    }

    sealed interface Server: TorrserverError {
        data class PlatformNotSupported(val message: String): Server
        data class FileNotExist(val message: String): Server
        data object NotStarted: Server
        data object NoServerConnection: Server
    }

    data class Unknown(val message: String): TorrserverError
}