package com.dik.torrserverapi.domain.usecases

import com.dik.torrserverapi.domain.filedownloader.DownloadFileRusult
import com.dik.torrserverapi.domain.filedownloader.FileDownloader
import kotlinx.coroutines.flow.Flow

internal class DownloadFileUseCase(
    private val fileDownloader: FileDownloader,
) {

    operator fun invoke(
        fileUrl: String,
        outputFilePath: String
    ): Flow<DownloadFileRusult> {
        return fileDownloader.downloadFile(fileUrl, outputFilePath)
    }
}