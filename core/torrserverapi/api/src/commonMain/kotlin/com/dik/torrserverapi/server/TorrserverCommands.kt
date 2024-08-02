package com.dik.torrserverapi.server

import com.dik.common.Result
import com.dik.common.ResultProgress
import com.dik.torrserverapi.TorrserverError
import com.dik.torrserverapi.model.TorrserverFile
import kotlinx.coroutines.flow.Flow
import java.io.File

interface TorrserverCommands {
    suspend fun installServer(pathToFile: String): Flow<ResultProgress<TorrserverFile, TorrserverError>>
    suspend fun startServer(pathToServerFile: String): Result<Unit, TorrserverError>
    suspend fun stopServer(): Result<Unit, TorrserverError>
}