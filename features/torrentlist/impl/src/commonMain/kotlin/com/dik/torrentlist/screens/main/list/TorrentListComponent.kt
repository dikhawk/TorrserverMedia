package com.dik.torrentlist.screens.main.list

import androidx.compose.runtime.Stable
import com.dik.torrentlist.screens.model.TorrentUiState
import kotlinx.coroutines.flow.StateFlow


internal interface TorrentListComponent {

    val uiState: StateFlow<TorrentListState>
    fun onClickItem(torrent: TorrentUiState)
    fun onNavigateToDetails(torrent: TorrentUiState)
    fun onClickDeleteItem(torrent: TorrentUiState)
    fun addTorrents(paths: List<String>)
    fun addMagnet(magnetLink: String)
}

@Stable
internal data class TorrentListState(
    val torrents: List<TorrentUiState> = emptyList(),
    val isShowProgress: Boolean = false,
    val error: String? = null,
)
