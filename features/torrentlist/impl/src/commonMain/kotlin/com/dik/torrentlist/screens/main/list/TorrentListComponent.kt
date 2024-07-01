package com.dik.torrentlist.screens.main.list

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import com.dik.torrserverapi.Torrent
import kotlinx.coroutines.flow.StateFlow

interface TorrentListComponent {

    val uiState: StateFlow<TorrentListState>
    fun onClickItem(torrent: Torrent)
}

@Stable
data class TorrentListState(
    val torrents: MutableList<Torrent> = mutableStateListOf(),
    val isLoading: Boolean = false,
    val error: String? = null
)