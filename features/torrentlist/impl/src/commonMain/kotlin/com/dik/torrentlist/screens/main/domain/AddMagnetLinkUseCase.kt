package com.dik.torrentlist.screens.main.domain

import com.dik.common.Result
import com.dik.common.errors.Error
import com.dik.common.i18n.LocalizationResource
import com.dik.common.onError
import com.dik.common.onSuccess
import com.dik.torrentlist.error.toMessage
import com.dik.torrentlist.utils.isValidMagnetLink
import com.dik.torrserverapi.model.Torrent
import com.dik.torrserverapi.server.api.MagnetApi
import com.dik.torrserverapi.server.api.TorrentApi

internal class AddMagnetLinkUseCase(
    private val magnetApi: MagnetApi,
    private val torrentApi: TorrentApi,
    private val findThumbnailForTorrent: FindPosterUseCase,
    private val localization: LocalizationResource
) {
    suspend operator fun invoke(magnetLink: String): Result<Torrent, AddMagnetLinkErrors> {
        if (!magnetLink.isValidMagnetLink())
            return Result.Error(AddMagnetLinkErrors.InvalidMagnet)

        magnetApi.addMagnet(magnetUrl = magnetLink)
            .onError { error ->
                return Result.Error(AddMagnetLinkErrors.UnknownError(error.toMessage(localization)))
            }.onSuccess { resultData ->
                var torrent = resultData

                findThumbnailForTorrent.invoke(torrent)
                    .onSuccess { data ->
                        if (!data.poster300.isNullOrEmpty()) {
                            torrent = torrent.copy(poster = data.poster300)
                            torrentApi.updateTorrent(torrent)
                        }
                    }

                return Result.Success(torrent)
            }

        return Result.Error(AddMagnetLinkErrors.InvalidMagnet)
    }
}

internal interface AddMagnetLinkErrors : Error {
    data object InvalidMagnet : AddMagnetLinkErrors
    data class UnknownError(val error: String) : AddMagnetLinkErrors
}