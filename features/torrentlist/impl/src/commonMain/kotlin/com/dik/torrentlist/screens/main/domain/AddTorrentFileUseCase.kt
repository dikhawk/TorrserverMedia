package com.dik.torrentlist.screens.main.domain

import com.dik.common.Result
import com.dik.common.errors.Error
import com.dik.common.onError
import com.dik.common.onSuccess
import com.dik.torrentlist.utils.FileUtils
import com.dik.torrserverapi.model.Torrent
import com.dik.torrserverapi.server.api.TorrentApi

internal class AddTorrentFileUseCase(
    private val torrentApi: TorrentApi,
    private val findThumbnailForTorrent: FindPosterUseCase,
    private val fileUtils: FileUtils,
) {
    suspend operator fun invoke(path: String): Result<Torrent, AddTorrentFileErrors> {
        if (!fileUtils.isFileExist(path)) return Result.Error(AddTorrentFileErrors.TorrentNotExist)

        torrentApi.addTorrent(path).onSuccess { data ->
            var torrent = data
            findThumbnailForTorrent.invoke(data)
                .onSuccess { poster ->
                    if (!poster.poster300.isNullOrEmpty()) {
                        torrent = torrent.copy(poster = poster.poster300)
                        torrentApi.updateTorrent(torrent)
                    }

                    return Result.Success(torrent)
                }.onError { error ->
                    return Result.Error(AddTorrentFileErrors.UnknownError(error.toString()))
                }
        }

        return Result.Error(AddTorrentFileErrors.InvalidTorrent)
    }
}

internal interface AddTorrentFileErrors : Error {
    data object TorrentNotExist : AddTorrentFileErrors
    data object InvalidTorrent : AddTorrentFileErrors
    data class UnknownError(val error: String) : AddTorrentFileErrors
}