package com.dik.torrserverapi.server

import com.dik.common.Result
import com.dik.common.ResultProgress
import com.dik.torrserverapi.TorrserverError
import com.dik.torrserverapi.model.TorrserverFile
import com.dik.torrserverapi.model.TorrserverStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

interface TorrserverCommands {
    suspend fun serverStatus(): SharedFlow<TorrserverStatus>
    suspend fun installServer(): Flow<ResultProgress<TorrserverFile, TorrserverError>>
    suspend fun startServer(): Result<Unit, TorrserverError>
    suspend fun stopServer(): Result<Unit, TorrserverError>
    suspend fun isServerInstalled(): Result<Boolean, TorrserverError>
    suspend fun isServerStarted(): Result<Boolean, TorrserverError>
    suspend fun isAvailableNewVersion(): Result<Boolean, TorrserverError>
    suspend fun restoreServerFromBackUp(): Result<Unit, TorrserverError>
}