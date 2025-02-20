package com.dik.torrentlist.screens.main

import com.dik.common.Result
import com.dik.common.i18n.LocalizationResource
import com.dik.common.utils.successResult
import com.dik.torrentlist.error.toMessage
import com.dik.torrentlist.utils.isValidMagnetLink
import com.dik.torrserverapi.model.Torrent
import com.dik.torrserverapi.server.MagnetApi
import com.dik.torrserverapi.server.TorrentApi
import torrservermedia.features.torrentlist.impl.generated.resources.Res
import torrservermedia.features.torrentlist.impl.generated.resources.main_add_dialog_error_invalid_magnet

internal class AddMagnetLink(
    private val magnetApi: MagnetApi,
    private val torrentApi: TorrentApi,
    private val findThumbnailForTorrent: FindPosterForTorrent,
    private val localization: LocalizationResource
) {
    suspend operator fun invoke(magnetLink: String): AddMagnetLinkResult {
        if (!magnetLink.isValidMagnetLink())
            return AddMagnetLinkResult(error = localization.getString(Res.string.main_add_dialog_error_invalid_magnet))

        return when (val result = magnetApi.addMagnet(magnetUrl = magnetLink)) {
            is Result.Error -> AddMagnetLinkResult(error = result.error.toMessage(localization))
            is Result.Success -> {
                var torrent = result.data
                val poster = findThumbnailForTorrent.invoke(result.data).successResult()

                if (!poster?.poster300.isNullOrEmpty()) {
                    torrent = torrent.copy(poster = poster?.poster300!!)
                    torrentApi.updateTorrent(torrent)
                }
                AddMagnetLinkResult(torrent = torrent)
            }
        }
    }
}

internal data class AddMagnetLinkResult(
    val error: String? = null,
    val torrent: Torrent? = null
)