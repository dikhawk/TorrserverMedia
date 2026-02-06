package com.dik.torrserverapi.domain.usecases

import co.touchlab.kermit.Logger
import com.dik.common.Platform
import com.dik.common.Result
import com.dik.common.utils.cpuArch
import com.dik.common.utils.platformName
import com.dik.torrserverapi.TorrserverError
import com.dik.torrserverapi.domain.filedownloader.DownloadFileRusult
import com.dik.torrserverapi.model.Asset
import com.dik.torrserverapi.model.Release
import com.dik.torrserverapi.server.ServerConfig
import com.dik.torrserverapi.server.TorrserverStatus
import com.dik.torrserverapi.server.api.TorrserverApiClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

internal class InstallTorrserverUseCase(
    private val torrserverApiClient: TorrserverApiClient,
    private val downloadFile: DownloadFileUseCase,
    private val backupFile: BackupFileUseCase,
    private val restoreServerFromBackUp: RestoreServerFromBackUpUseCase,
    private val config: ServerConfig,
) {
    private val tag = "InstallTorrserver:"

    operator fun invoke(
        cpuArch: String = cpuArch(),
        platform: Platform = platformName(),
        outputFilePath: String = config.pathToServerFile,
        outputBackupFilePath: String = config.pathToBackupServerFile
    ): Flow<TorrserverStatus> = flow {
        emit(TorrserverStatus.Install.Installing)
        Logger.i("$tag started installing")

        val latestRelease = torrserverApiClient.checkLatestRelease()

        if (latestRelease is Result.Error) {
            emit(TorrserverStatus.Install.Error(latestRelease.error.toString()))
            return@flow
        }

        val asset = findSupportedAsset(latestRelease, cpuArch, platform)

        if (asset == null) {
            emitPlatformNotSupported(cpuArch, platform)
            return@flow
        }

        backupFile(outputFilePath, outputBackupFilePath)

        emitAll(downloadAndMapStatus(asset, outputFilePath, outputBackupFilePath))
    }.catch { e ->
        Logger.e("$tag $e")
        emit(TorrserverStatus.Install.Error(e.toString()))
    }

    private fun downloadAndMapStatus(
        asset: Asset,
        outputFilePath: String,
        outputBackupFilePath: String
    ): Flow<TorrserverStatus> =
        downloadFile(asset.browserDownloadUrl, outputFilePath)
            .map { it.mapToTorrserverStatus() }
            .onEach { status ->
                if (status is TorrserverStatus.Install.Error) {
                    restoreServerFromBackUp(outputBackupFilePath, outputFilePath)
                }
            }

    private suspend fun FlowCollector<TorrserverStatus>.emitPlatformNotSupported(
        cpuArch: String,
        platform: Platform
    ) {
        val msg = "Not supported os: ${platform.osname} or arch $cpuArch"
        Logger.i("$tag $msg")
        emit(TorrserverStatus.Install.PlatformNotSupported(msg))
    }

    private fun findSupportedAsset(
        latestRelease: Result<Release, TorrserverError>,
        cpuArch: String,
        platform: Platform
    ): Asset? = (latestRelease as? Result.Success)?.data?.assets?.find {
        it.name.contains(cpuArch, ignoreCase = true) &&
                it.name.contains(platform.osname, ignoreCase = true)
    }

    private fun DownloadFileRusult.mapToTorrserverStatus(): TorrserverStatus {
        return when (this) {
            is DownloadFileRusult.Erorr ->
                TorrserverStatus.Install.Error(type.toString())

            is DownloadFileRusult.Progress ->
                TorrserverStatus.Install.Progress(value)

            is DownloadFileRusult.Done ->
                TorrserverStatus.Install.Installed

            DownloadFileRusult.Starting ->
                TorrserverStatus.Install.Installing
        }
    }
}