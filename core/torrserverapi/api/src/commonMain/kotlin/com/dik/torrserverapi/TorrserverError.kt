package com.dik.torrserverapi

import com.dik.common.errors.Error

sealed interface TorrserverError : Error {

    sealed interface Common: TorrserverError {
        data class Unknown(val messeage: String): Common
        object ResponseReturnNull: Common
    }

    sealed interface Service: TorrserverError {
        data class NotSupported(val messeage: String): Service
    }
}