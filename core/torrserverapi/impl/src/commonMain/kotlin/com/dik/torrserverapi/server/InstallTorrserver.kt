package com.dik.torrserverapi.server

import com.dik.common.AppDispatchers
import com.dik.common.Platform
import com.dik.common.Result
import com.dik.common.utils.cpuArch
import com.dik.common.utils.platformName
import com.dik.torrserverapi.TorrserverError
import com.dik.torrserverapi.model.Asset
import com.dik.torrserverapi.model.Release
import com.dik.torrserverapi.model.TorrserverFile
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

internal class InstallTorrserver(
    private val torrserverStuffApi: TorrserverStuffApi,
    private val downloadFile: DownloadFile,
    private val dispatchers: AppDispatchers
) {

    fun start(outputFilePath: String) = flow<Result<TorrserverFile, TorrserverError>> {
        val latestRelease = torrserverStuffApi.checkLatestRelease()

        if (latestRelease is Result.Error) {
            emit(Result.Error(latestRelease.error))
            return@flow
        }

        val cpuArh = cpuArch()
        val platform = platformName()
        val asset = if (latestRelease is Result.Success)
            latestRelease.data.findSupportedAsset(cpuArh, platform) else null

        if (asset == null) {
            emit(Result.Error(TorrserverError.Service.NotSupported("Not os: ${platform.osname} or arch $cpuArh")))
            return@flow
        }

        downloadFile.start(
            fileUrl = asset.browserDownloadUrl,
            outputFilePath = outputFilePath
        ).collect { result ->
            emit(result)
        }
    }.catch { e ->
        emit(Result.Error(TorrserverError.Common.Unknown(e.toString())))
    }.flowOn(dispatchers.defaultDispatcher())

    //TODO requred testing for other platforms
    private fun Release.findSupportedAsset(cpuArch: String, platform: Platform): Asset? {
        return assets.find { asset ->
            asset.name.contains(cpuArch, ignoreCase = true) &&
                    asset.name.contains(platform.osname, ignoreCase = true)
        }
    }
}

