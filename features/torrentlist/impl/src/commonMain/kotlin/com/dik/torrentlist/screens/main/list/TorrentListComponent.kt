package com.dik.torrentlist.screens.main.list

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import kotlinx.coroutines.flow.StateFlow

interface TorrentListComponent {

    val torrenListState: StateFlow<TorrentListState>
    fun onClickItem(torrent: Torrent)
}

@Stable
data class TorrentListState(
    val torrents: List<Torrent> = mutableStateListOf(),
    val isLoading: Boolean = false,
)

data class Torrent(
    val url: String
)