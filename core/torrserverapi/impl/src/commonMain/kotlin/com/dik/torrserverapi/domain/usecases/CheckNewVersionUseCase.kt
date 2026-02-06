package com.dik.torrserverapi.domain.usecases

import com.dik.common.Result
import com.dik.torrserverapi.server.TorrserverStatus
import com.dik.torrserverapi.server.api.TorrserverApiClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class CheckNewVersionUseCase(
    private val torrserverApiClient: TorrserverApiClient,
) {
    operator fun invoke(): Flow<TorrserverStatus> = flow {
        emit(TorrserverStatus.CheckLatesVersion.Checking)
        val serverStatusResult = torrserverApiClient.echo()

        val latestVersionResult = torrserverApiClient.checkLatestRelease()

        if (latestVersionResult is Result.Error) {
            emit(TorrserverStatus.CheckLatesVersion.Error(latestVersionResult.error.toString()))
            return@flow
        }

        if (serverStatusResult is Result.Success && latestVersionResult is Result.Success) {
            val localServerVersion = serverStatusResult.data
            val latestServerVersion = latestVersionResult.data.tagName

            if (localServerVersion != latestServerVersion) {
                emit(TorrserverStatus.CheckLatesVersion.AvaliableNewVersion(latestServerVersion))
            } else {
                emit(TorrserverStatus.CheckLatesVersion.VersionIsActual)
            }
        }
    }
}