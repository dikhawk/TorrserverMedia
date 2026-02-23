package com.dik.torrentlist.screens.main.list

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import com.dik.torrentlist.screens.model.TorrentUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow


internal interface TorrentListComponent {

    val uiState: StateFlow<TorrentListState>
    fun onClickItem(torrent: TorrentUiState)
    fun onNavigateToDetails(torrent: TorrentUiState)
    fun onClickDeleteItem(torrent: TorrentUiState)
    fun addTorrents(paths: List<String>)
    fun addMagnet(magnetLink: String)
    fun observeTorrentsList(): Flow<List<TorrentUiState>>
}

@Stable
internal data class TorrentListState(
    val torrents: MutableList<TorrentUiState> = mutableStateListOf(),
    val isShowProgress: Boolean = false,
    val error: String? = null,
)
