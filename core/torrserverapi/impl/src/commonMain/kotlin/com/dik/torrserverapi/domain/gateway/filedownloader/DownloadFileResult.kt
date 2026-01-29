package com.dik.torrserverapi.domain.gateway.filedownloader

import com.dik.torrserverapi.TorrserverError

internal sealed interface DownloadFileRusult {
    data object Starting : DownloadFileRusult
    data class Progress(val value: Double) : DownloadFileRusult
    data object Done : DownloadFileRusult
    data class Erorr(val type: TorrserverError) : DownloadFileRusult
}