package com.dik.torrserverapi

import com.dik.common.errors.Error

sealed interface TorrserverError : Error {

    sealed interface Common: TorrserverError {
        data class Unknown(val messeage: String): Common
    }
}