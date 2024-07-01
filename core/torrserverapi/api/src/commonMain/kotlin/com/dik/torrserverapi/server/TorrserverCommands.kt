package com.dik.torrserverapi.server

import com.dik.common.Result
import com.dik.common.ResultProgress
import com.dik.torrserverapi.TorrserverError
import com.dik.torrserverapi.model.TorrserverFile
import kotlinx.coroutines.flow.Flow
import java.io.File

interface TorrserverCommands {
    suspend fun installServer(): Flow<ResultProgress<TorrserverFile, TorrserverError>>
    suspend fun startServer(): Result<Unit, TorrserverError>
    suspend fun stopServer(): Result<Unit, TorrserverError>
}