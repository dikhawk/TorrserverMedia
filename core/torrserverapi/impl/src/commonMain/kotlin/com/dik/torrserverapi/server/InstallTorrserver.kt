package com.dik.torrserverapi.server

import com.dik.common.AppDispatchers
import com.dik.common.Platform
import com.dik.common.Result
import com.dik.common.ResultProgress
import com.dik.common.utils.cpuArch
import com.dik.common.utils.platformName
import com.dik.torrserverapi.TorrserverError
import com.dik.torrserverapi.model.Asset
import com.dik.torrserverapi.model.Release
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

internal class InstallTorrserver(
    private val torrserverStuffApi: TorrserverStuffApi,
    private val downloadFile: DownloadFile,
    private val backupFile: BackupFile,
    private val restoreServerFromBackUp: RestoreServerFromBackUp,
    private val dispatchers: AppDispatchers
) {

    operator fun invoke(
        outputFilePath: String, outputBackupFilePath: String
    ) = flow {
        val latestRelease = torrserverStuffApi.checkLatestRelease()

        if (latestRelease is Result.Error) {
            emit(ResultProgress.Error(latestRelease.error))
            return@flow
        }

        val cpuArh = cpuArch()
        val platform = platformName()
        val asset = if (latestRelease is Result.Success)
            latestRelease.data.findSupportedAsset(cpuArh, platform) else null

        if (asset == null) {
            emit(ResultProgress.Error(TorrserverError.Server.PlatformNotSupported("Not os: ${platform.osname} or arch $cpuArh")))
            return@flow
        }

        backupFile(outputFilePath, outputBackupFilePath)

        downloadFile.invoke(
            fileUrl = asset.browserDownloadUrl,
            outputFilePath = outputFilePath
        ).collect { result ->
            emit(result)
        }
    }.catch { e ->
        restoreServerFromBackUp(outputBackupFilePath, outputFilePath)
        emit(ResultProgress.Error(TorrserverError.Unknown(e.toString())))
    }.flowOn(dispatchers.defaultDispatcher())

    private fun Release.findSupportedAsset(cpuArch: String, platform: Platform): Asset? {
        return assets.find { asset ->
            asset.name.contains(cpuArch, ignoreCase = true) &&
                    asset.name.contains(platform.osname, ignoreCase = true)
        }
    }
}

