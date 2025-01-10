package com.dik.torrentlist.screens.main.list

import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import com.dik.torrserverapi.model.Torrent
import kotlinx.coroutines.flow.StateFlow


interface TorrentListComponent {

    val uiState: StateFlow<TorrentListState>
    fun onClickItem(torrent: Torrent)
    fun onClickDeleteItem(torrent: Torrent)
    fun addTorrents(paths: List<String>)
}

@Stable
data class TorrentListState(
    val torrents: MutableList<Torrent> = mutableStateListOf(),
    val isShowProgress: Boolean = false,
    val error: String? = null,
)