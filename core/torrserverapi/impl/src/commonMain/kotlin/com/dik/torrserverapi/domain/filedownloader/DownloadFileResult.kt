package com.dik.torrserverapi.domain.filedownloader

import com.dik.torrserverapi.TorrserverError

internal sealed interface DownloadFileRusult {
    data object Starting : DownloadFileRusult
    data class Progress(val progress: Double, val currentBytes: Long, val totalBytes: Long) : DownloadFileRusult
    data object Done : DownloadFileRusult
    data class Erorr(val type: TorrserverError) : DownloadFileRusult
}