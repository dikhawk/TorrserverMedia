package com.dik.torrserverapi

import com.dik.common.errors.Error

sealed interface TorrserverError : Error {

    sealed interface HttpError: TorrserverError {
        object ResponseReturnNull: HttpError
    }

    sealed interface Server: TorrserverError {
        data class PlatformNotSupported(val messeage: String): Server
        data class FileNotExist(val message: String): Server
        object NotStarted: Server
    }

    data class Unknown(val messeage: String): TorrserverError
}