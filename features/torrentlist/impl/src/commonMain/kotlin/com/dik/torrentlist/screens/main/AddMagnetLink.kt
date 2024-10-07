package com.dik.torrentlist.screens.main

import com.dik.common.Result
import com.dik.torrentlist.error.toMessage
import com.dik.torrserverapi.model.Torrent
import com.dik.torrserverapi.server.MagnetApi
import com.dik.torrserverapi.server.TorrentApi
import org.jetbrains.compose.resources.getString
import torrservermedia.features.torrentlist.impl.generated.resources.Res
import torrservermedia.features.torrentlist.impl.generated.resources.main_add_dialog_error_invalid_magnet

internal class AddMagnetLink(
    private val magnetApi: MagnetApi,
    private val torrentApi: TorrentApi,
    private val findThumbnailForTorrent: FindThumbnailForTorrent,
) {
    suspend operator fun invoke(magnetLink: String): AddMagnetLinkResult {
        if (!magnetLink.isValidMagnetLink())
            return AddMagnetLinkResult(error = getString(Res.string.main_add_dialog_error_invalid_magnet))

        return when (val result = magnetApi.addMagnet(magnetUrl = magnetLink)) {
            is Result.Error -> AddMagnetLinkResult(error = result.error.toMessage())
            is Result.Success -> {
                var torrent = result.data
                val thumbnailResult = findThumbnailForTorrent.invoke(result.data)

                if (!thumbnailResult.thumbnail.isNullOrEmpty()) {
                    torrent = torrent.copy(poster = thumbnailResult.thumbnail)
                    torrentApi.updateTorrent(torrent)
                }
                AddMagnetLinkResult(torrent = torrent)
            }
        }
    }

    private fun String.isValidMagnetLink(): Boolean {
        val magnetUriRegex = Regex("^magnet:\\?xt=urn:[a-z0-9]+:[a-zA-Z0-9]{32,40}(&.+)?$")
        return magnetUriRegex.matches(this)
    }
}

internal data class AddMagnetLinkResult(
    val error: String? = null,
    val torrent: Torrent? = null
)