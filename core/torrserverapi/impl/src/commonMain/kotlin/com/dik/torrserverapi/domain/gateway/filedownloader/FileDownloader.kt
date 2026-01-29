package com.dik.torrserverapi.domain.gateway.filedownloader

import kotlinx.coroutines.flow.Flow

internal interface FileDownloader {
    fun downloadFile(url: String, outputPath: String): Flow<DownloadFileRusult>
}