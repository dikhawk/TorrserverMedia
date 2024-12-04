package com.dik.torrentlist.screens.main

import com.dik.common.Result
import com.dik.common.utils.successResult
import com.dik.torrentlist.error.toMessage
import com.dik.torrentlist.utils.isFileExist
import com.dik.torrserverapi.model.Torrent
import com.dik.torrserverapi.server.TorrentApi
import org.jetbrains.compose.resources.getString
import torrservermedia.features.torrentlist.impl.generated.resources.Res
import torrservermedia.features.torrentlist.impl.generated.resources.main_torrent_list_add_torrent_file_not_exist

internal class AddTorrentFile(
    private val torrentApi: TorrentApi,
    private val findThumbnailForTorrent: FindPosterForTorrent,
) {
    suspend operator fun invoke(path: String): AddTorrentResult {
        if (!path.isFileExist()) return AddTorrentResult(
            error = getString(Res.string.main_torrent_list_add_torrent_file_not_exist)
        )

        return when (val result = torrentApi.addTorrent(path)) {
            is Result.Error -> AddTorrentResult(
                error = result.error.toMessage()
            )

            is Result.Success -> {
                var torrent = result.data
                val poster = findThumbnailForTorrent.invoke(result.data).successResult()

                if (!poster?.poster300.isNullOrEmpty()) {
                    torrent = torrent.copy(poster = poster?.poster300!!)
                    torrentApi.updateTorrent(torrent)
                }
                AddTorrentResult(torrent = torrent)
            }
        }
    }
}

internal data class AddTorrentResult(
    val torrent: Torrent? = null,
    val error: String? = null,
)